package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.core.model.road.RoadPath;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.core.model.time.TimeModel;
import com.github.rinde.rinsim.geom.GeomHeuristic;
import com.github.rinde.rinsim.geom.GeomHeuristics;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.base.Optional;
import org.apache.commons.math3.random.RandomGenerator;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Velocity;
import java.util.*;

import static DelgMas.Battery.CHARGING_DURATION;

public class AgvAgent extends Vehicle implements TickListener, RoadUser {
    public static final int POWERLIMIT = 500;
    public static final double SPEED = 1;
    private static final int CAPACITY = 2;
    private static final double POWERCONSUME = 0.1;

    private RandomGenerator rng;
    private AgvModel agvModel;
    private DMASModel dmasModel;

    public boolean hasBattery;
    private Optional<Parcel> target;
    private Queue<Point> path;

    AgvAgent(Point startPosition, RandomGenerator r, AgvModel agv, DMASModel dmas) {
        super(VehicleDTO.builder()
                .capacity(CAPACITY)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());

        this.agvModel = agv;
        this.dmasModel = dmas;
        this.rng = r;

        target = Optional.absent();
        path = new LinkedList<>();
        this.hasBattery = false;

    }


    void nextDestination(TimeLapse timeLapse) {
        if (this.agvModel.getVehicleState(this).equals(PDPModel.VehicleState.IDLE)) {
            if (getBattery().capacity > 20) {
                Collection<Parcel> parcels = this.agvModel.getParcels(PDPModel.ParcelState.AVAILABLE);
                Optional<Box> curr = Optional.fromNullable(RoadModels.findClosestObject(
                        this.getRoadModel().getPosition(this), this.getRoadModel(), Box.class));
                if (curr.isPresent())
                    this.target = Optional.of((Parcel) curr.get());
            } else {
                BatteryCharger batteryCharger = RoadModels.findClosestObject(this.getRoadModel().getPosition(this), this.getRoadModel(), BatteryCharger.class);
                Battery battery = getBattery();
                battery.destination = batteryCharger.position;

                battery.setTimewindow(timeLapse.getTime());
                this.target = Optional.of((Parcel) battery);
            }
        }
    }

    void pickupBox(TimeLapse tm) {
        //System.out.println(getRoadModel().getObjectsOfType(Box.class));
        //System.out.println("Pick up");
        this.agvModel.pickup(this, target.get(), tm);
        //System.out.println(getRoadModel().getObjectsOfType(Box.class));

    }

    void pickupBattery(TimeLapse tm) {
        this.agvModel.pickup(this, RoadModels.findClosestObject(this.getRoadModel().getPosition(this), this.getRoadModel(), Battery.class), tm);
    }

    void moveBattery(TimeLapse tm) {
        Battery battery = (Battery) this.target.get();
        List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, battery.destination);

        BatteryCharger charger = getBatteryCharger(battery.destination);

        Queue<Point> queue = new LinkedList<>(shortestPathTo);

        int result = dmasModel.releaseAnts_B(queue, target.get().getDeliveryTimeWindow());
        if(result==-1){
            System.out.println("The path is already booked");
        }
        dmasModel.releaseAnts_C(queue, target.get().getDeliveryTimeWindow());
        charger.bookBatteryCharger(target.get().getDeliveryTimeWindow());
        this.getRoadModel().followPath(this, queue, tm);
        this.getBattery().capacity -= POWERCONSUME;
    }

    void moveBox(TimeLapse tm){
        Point dest;
        if (this.agvModel.getContents(this).contains(target.get())) {
            dest = this.target.get().getDeliveryLocation();
        } else {
            dest = this.target.get().getPickupLocation();
        }

        List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, dest);
        Queue<Point> queue = new LinkedList<>(shortestPathTo);
        Queue<Queue<Point>> path = dmasModel.releaseAnts_D(shortestPathTo.get(1), dest);

        System.out.println(path);
        for(Queue<Point> path2 : path){
            //System.out.println(path2);
            int result = dmasModel.releaseAnts_B(path.element(), target.get().getDeliveryTimeWindow());
            if(result!=-1){
                dmasModel.releaseAnts_C(queue, target.get().getDeliveryTimeWindow());
                this.getRoadModel().followPath(this, queue, tm);
                break;
            }
        }

        this.getBattery().capacity -= POWERCONSUME;
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {

    }

    @Override
    protected void tickImpl(TimeLapse timeLapse) {
        //First task is every agent to load the battery they have.
        //Battery battery = getBattery();

        //System.out.println(getRoadModel().getObjectsOfType(Box.class).size());
        if (!this.hasBattery) {
            this.hasBattery = true;
            pickupBattery(timeLapse);
            //System.out.println("Battery added");
            return;
        }

        if (!timeLapse.hasTimeLeft()) {
            return;
        }
        if (!target.isPresent())
            this.nextDestination(timeLapse);

        if (target.isPresent()) {
            if (is_delivering_box()) {
                if (!has_delivering_box() && is_in_pick_destination()) {
                    pickupBox(timeLapse);
                } else if (has_delivering_box() && is_in_delivery_destination()) {
                    this.agvModel.store_box(this, (Box) target.get(), timeLapse, rng);
                    target = Optional.absent();
                } else if (this.agvModel.containerContains(this, target.get())) {
                    moveBox(timeLapse);
                } else {
                    if (getRoadModel().containsObject(target.get())) {
                        moveBox(timeLapse);
                    } else {
                        target = Optional.absent();
                    }
                }
            } else {
                if(has_delivering_battery() && is_in_charger_delivery_destination()) {
                    dropBattery((Battery) target.get(), timeLapse);
                }else if(!has_delivering_battery() && is_in_charger_delivery_destination()){
                    updateBattery(timeLapse);
                    target = Optional.absent();
                }else if (this.agvModel.containerContains(this, target.get())) {
                    moveBattery(timeLapse);
                } else {
                    if (getRoadModel().containsObject(target.get())) {
                        moveBattery(timeLapse);
                    } else {
                        target = Optional.absent();
                    }
                }
            }
        }
    }

    public void dropBattery(Battery battery, TimeLapse timeLapse) {
        this.agvModel.drop(this, battery, timeLapse);
        this.agvModel.unregister(battery);
    }
    public void updateBattery(TimeLapse timeLapse){
        Point position = this.getRoadModel().getPosition(this);
        Battery newbattery = new Battery(position);
        this.agvModel.registerBattery(newbattery);
        this.agvModel.pickup(this, newbattery, timeLapse);
    }

    public boolean is_in_charger_delivery_destination(){
        Battery battery = (Battery) this.target.get();
        return this.getRoadModel().getPosition(this).equals(battery.destination);
    }
    public boolean is_in_delivery_destination(){
        return this.getRoadModel().getPosition(this).equals(target.get().getDeliveryLocation());
    }

    public boolean is_in_pick_destination(){
        return this.getRoadModel().getPosition(this).equals(target.get().getPickupLocation());
    }

    public boolean is_delivering_box(){
        if(this.target.get().getDeliveryDuration()==CHARGING_DURATION){
            return false;
        }
        return true;
    }

    public boolean has_delivering_box(){
        return this.agvModel.containerContains(this, target.get());
    }

    public boolean has_delivering_battery(){
        return this.agvModel.containerContains(this, target.get());
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

    public Battery getBattery() {
        Battery battery = null;
        ArrayList<Parcel> contents = new ArrayList(this.agvModel.getContents(this));
        if (contents.size() >= 1) {
            battery = (Battery) contents.get(0);
        }
        return battery;
    }
    public BatteryCharger getBatteryCharger(Point position){
        BatteryCharger charger = null;
        Set<BatteryCharger> chargerers = this.getRoadModel().getObjectsOfType(BatteryCharger.class);
        for(BatteryCharger charg: chargerers){
            if(charg.position.equals(position)){
                charger=charg;
            }
        }
        return charger;
    }
}
