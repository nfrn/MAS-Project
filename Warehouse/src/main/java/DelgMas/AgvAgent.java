package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.road.*;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.core.model.time.TimeModel;
import com.github.rinde.rinsim.geom.GeomHeuristic;
import com.github.rinde.rinsim.geom.GeomHeuristics;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.base.Optional;
import jdk.nashorn.internal.objects.Global;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.*;

import static DelgMas.Battery.CHARGING_DURATION;

public class AgvAgent extends Vehicle implements TickListener, RoadUser {
    public static final int POWERLIMIT = 500;
    public static final double SPEED = 1;
    private static final int CAPACITY = 2;
    private static final double POWERCONSUME = 0.1;

    public int ID;

    private RandomGenerator rng;
    private AgvModel agvModel;
    private DMASModel dmasModel;

    public boolean hasBattery;
    private Optional<Parcel> target;
    private Queue<Point> path;

    public AgvAgent(Point startPosition, RandomGenerator r, AgvModel agv, DMASModel dmas, int id) {
        super(VehicleDTO.builder()
                .capacity(CAPACITY)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());

        ID = id;
        this.agvModel = agv;
        this.dmasModel = dmas;
        this.rng = r;

        target = Optional.absent();
        path = new LinkedList<>();
        this.hasBattery = false;

    }


    private void nextDestination(TimeLapse timeLapse) {
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

    private void pickupBox(TimeLapse tm) {
        //System.out.println(getRoadModel().getObjectsOfType(Box.class));
        //System.out.println("Pick up");
        this.agvModel.pickup(this, target.get(), tm);
        //System.out.println(getRoadModel().getObjectsOfType(Box.class));

    }

    private void pickupBattery(TimeLapse tm) {
        this.agvModel.pickup(this, RoadModels.findClosestObject(this.getRoadModel().getPosition(this), this.getRoadModel(), Battery.class), tm);
    }

    private void moveBattery(TimeLapse tm) {
        Battery battery = (Battery) this.target.get();
        List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, battery.destination);

        BatteryCharger charger = getBatteryCharger(battery.destination);

        Queue<Point> queue = new LinkedList<>(shortestPathTo);

        List<TimeWindow> tws = getTimeWindowsForPath(shortestPathTo, tm);
        int result = dmasModel.releaseAnts_B(queue, tws, this.ID);
        if(result==-1){
            System.out.println("The path is already booked");
        }
        charger.bookBatteryCharger(target.get().getDeliveryTimeWindow());
        this.getRoadModel().followPath(this, queue, tm);
        this.getBattery().capacity -= POWERCONSUME;
    }

    private void moveBox(TimeLapse tm){
        Point dest;
        if (this.agvModel.getContents(this).contains(target.get())) {
            dest = this.target.get().getDeliveryLocation();
        } else {
            dest = this.target.get().getPickupLocation();
        }

        List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, dest);
        Queue<Point> queue = new LinkedList<>(shortestPathTo);
        Queue<Queue<Point>> path = dmasModel.releaseAnts_D(shortestPathTo.get(1), dest); // TODO Why you tkae here element 1 and not 0?

        System.out.println(path);
        for(Queue<Point> path2 : path){
            //System.out.println(path2);
            List<TimeWindow> tws = getTimeWindowsForPath(new ArrayList<Point>(path.element()), tm);
            int result = dmasModel.releaseAnts_B(path.element(), tws, this.ID);
            if(result!=-1){
                dmasModel.releaseAnts_C(path.element(), tws, this.ID);
                this.getRoadModel().followPath(this, path.element(), tm);
                break;
            } else {
                path.remove();
            }
        }
        /**


        List<TimeWindow> tws = getTimeWindowsForPath(shortestPathTo, tm);

        int result = dmasModel.releaseAnts_B(queue, tws, this.ID);
        if(result==-1){
            System.out.println("The path is already booked");
        }

        dmasModel.releaseAnts_C(queue, tws, this.ID);

        try {
            this.getRoadModel().followPath(this, queue, tm);
        } catch (DeadlockException e) {
            for(PheromoneStorage phs : this.dmasModel.pheromoneStorageMap.values()) {
                if(phs.position.equals(shortestPathTo.get(1))){
                    System.out.println(phs.list_phero_B);
                }
            }
            System.out.println("dead");
        }
         */
        this.getBattery().capacity -= POWERCONSUME;
    }

    private List<TimeWindow> getTimeWindowsForPath(List<Point> path, TimeLapse tm) {
        long VISIT_TIME_LENGTH = 6000;

        List<TimeWindow> timeWindows = new ArrayList<>();

        long addTime = tm.getTime();
        if(this.dmasModel.grm.getGraph().containsNode(path.get(0)))
            timeWindows.add(TimeWindow.create(addTime, addTime+VISIT_TIME_LENGTH));

        for(int i = 0; i < path.size()-1; i++) {
            Point a = path.get(i);
            Point b = path.get(i+1);
            List<Point> p = new ArrayList<>();

            p.add(a);
            p.add(b);

            double dist = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
            long sp = (long) this.getSpeed();
            double delta = dist / (sp*1000);
            long time = (long) (delta * 3600 * AgvExample.TICK_LENGTH + addTime);

            long timeA = (time - VISIT_TIME_LENGTH) < 0 ? 0 : (time - VISIT_TIME_LENGTH);
            long timeB = time + VISIT_TIME_LENGTH;

            timeWindows.add(TimeWindow.create(timeA, timeB));
            addTime += time;
        }

        return timeWindows;
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

    private  void dropBattery(Battery battery, TimeLapse timeLapse) {
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
