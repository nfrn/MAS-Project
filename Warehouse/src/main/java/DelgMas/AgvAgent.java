package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.road.*;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.base.Optional;
import org.apache.commons.math3.random.RandomGenerator;

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.BaseUnit;
import javax.measure.unit.ProductUnit;
import javax.measure.unit.Unit;
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

    private Optional<Queue<Point>> currentPath;

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
        this.currentPath = Optional.absent();

    }


    private void nextDestination(TimeLapse timeLapse) {
        if (this.agvModel.getVehicleState(this).equals(PDPModel.VehicleState.IDLE)) {
            if (getBattery().capacity > 20) {
                List<Box> boxes = this.agvModel.getAvailableBoxes();
                int closestBox = 0;

                Box box = RoadModels.findClosestObject(this.getRoadModel().getPosition(this), this.getRoadModel(), boxes);

                Optional<Box> curr = Optional.fromNullable(box);

                if (curr.isPresent())
                    this.target = Optional.of((Parcel) curr.get());
//                Collection<Parcel> parcels = this.agvModel.getParcels(PDPModel.ParcelState.AVAILABLE);
//                Optional<Box> curr = Optional.fromNullable(RoadModels.findClosestObject(
//                        this.getRoadModel().getPosition(this), this.getRoadModel(), Box.class));
//                if (curr.isPresent())
//                    this.target = Optional.of((Parcel) curr.get());
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
        this.agvModel.pickup(this, target.get(), tm);
        this.currentPath = Optional.absent();

    }

    private void pickupBattery(TimeLapse tm) {
        this.agvModel.pickup(this, RoadModels.findClosestObject(this.getRoadModel().getPosition(this), this.getRoadModel(), Battery.class), tm);
    }

    private void moveBattery(TimeLapse tm) {
        Battery battery = (Battery) this.target.get();
        List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, battery.destination);

        BatteryCharger charger = getBatteryCharger(battery.destination);
/*
        Point currLocation = this.getRoadModel().getPosition(this);
        if(this.dmasModel.grm.getGraph().containsNode(currLocation)) {
            this.dmasModel.grm.getGraph().
        }

        int result = -1;
        List<TimeWindow> tws = new ArrayList<>();
        Queue<Point> queue = new LinkedList<>();
        while (result == -1) {
            queue = new LinkedList<>();
            queue.add(this.getRoadModel().getPosition(this));
            queue.add(shortestPathTo.get(1));
            queue.addAll(dmasModel.releaseAnts_D(shortestPathTo.get(1), battery.destination));
            tws = getTimeWindowsForPath(new ArrayList<Point>(queue), tm);
            result = dmasModel.releaseAnts_B(queue, tws, this.ID);
        }
        dmasModel.releaseAnts_C(queue, tws, this.ID);
        this.currentPath = Optional.of(queue);
        */
        Queue<Point> queue = new LinkedList<>(shortestPathTo);

        List<TimeWindow> tws = getTimeWindowsForPath(shortestPathTo, tm);
        int result = dmasModel.releaseAnts_B(queue, tws, this.ID);
        if (result == -1) {
            System.out.println("The path is already booked");
        }

        charger.bookBatteryCharger(TimeWindow.create(tws.get(tws.size() - 1).begin(), tws.get(tws.size() - 1).end() + battery.getDeliveryDuration()));

        this.getRoadModel().followPath(this, queue, tm);
        this.getBattery().capacity -= POWERCONSUME;
    }

    private void moveBox(TimeLapse tm) {
        Point dest;
        if (this.agvModel.getContents(this).contains(target.get())) {
            dest = this.target.get().getDeliveryLocation();
        } else {
            dest = this.target.get().getPickupLocation();
        }

        System.out.println("processing");

        boolean getNewPath = false;
        if (this.currentPath.isPresent() && !this.currentPath.get().isEmpty()) {
            List<TimeWindow> tws = getTimeWindowsForPath(new ArrayList<Point>(this.currentPath.get()), tm);
            int result = dmasModel.releaseAnts_B(this.currentPath.get(), tws, this.ID);
            if (result == -1)
                getNewPath = true;
            else {
                // rebook the old path
                dmasModel.releaseAnts_C(this.currentPath.get(), tws, this.ID);
            }

        } else
            getNewPath = true;

        if (getNewPath) {
            //List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, dest);
            Point currPoint = this.getRoadModel().getPosition(this);
            Point nearestPoint = currPoint;
            if (!this.dmasModel.grm.getGraph().containsNode(currPoint)) {
                System.out.println("not on a node!!!!!!!");
                Connection c = this.dmasModel.grm.getConnection(this).get();
                nearestPoint = c.to();
            }

            int result = -1;
            List<TimeWindow> tws = new ArrayList<>();
            Queue<Point> queue = new LinkedList<>();

            int i = 0;
            while (result == -1) {
                System.out.println("searching path...");
                queue = new LinkedList<>();
                queue.add(currPoint);
                queue.add(nearestPoint);

                queue.addAll(dmasModel.releaseAnts_D(nearestPoint, dest));
                tws = getTimeWindowsForPath(new ArrayList<Point>(queue), tm);
                result = dmasModel.releaseAnts_B(queue, tws, this.ID);
                i++;
                if (i > 1000)
                    try {
                        System.out.println("sleep");
                        Thread.sleep(1000);
                        i = 0;
                    } catch (Exception e) {

                    }
            }
            dmasModel.releaseAnts_C(queue, tws, this.ID);
            this.currentPath = Optional.of(queue);
        }

        try {
            this.getRoadModel().followPath(this, this.currentPath.get(), tm);
        } catch (Exception e) {
            System.out.println("errr");
        }

        this.getBattery().capacity -= POWERCONSUME;
    }

    private List<TimeWindow> getTimeWindowsForPath(List<Point> path, TimeLapse tm) {
        long VISIT_TIME_LENGTH =8000;

        List<TimeWindow> timeWindows = new ArrayList<>();

        long addTime = tm.getTime();
        if (this.dmasModel.grm.getGraph().containsNode(path.get(0)))
            timeWindows.add(TimeWindow.create(addTime, addTime + VISIT_TIME_LENGTH));

        for (int i = 0; i < path.size() - 1; i++) {
            Point a = path.get(i);
            Point b = path.get(i + 1);
            List<Point> p = new ArrayList<>();

            p.add(a);
            p.add(b);

            double dist = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
            Measure<Double, Velocity> speed = Measure.valueOf(this.getSpeed(), this.getRoadModel().getSpeedUnit());
            Measure<Double, Length> distance = Measure.valueOf(dist, this.getRoadModel().getDistanceUnit());
            BaseUnit<Duration> duration = new BaseUnit<Duration>("s");
            double travelTime = RoadModels.computeTravelTime(speed, distance, duration);
            long time = AgvExample.TICK_LENGTH * (long) travelTime + addTime;
            long timeA = (time - VISIT_TIME_LENGTH) < 0 ? 0 : (time - VISIT_TIME_LENGTH);
            long timeB = time + VISIT_TIME_LENGTH;

            timeWindows.add(TimeWindow.create(timeA, timeB));
            addTime = time;
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
                    this.currentPath = Optional.absent();
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
                if (has_delivering_battery() && is_in_charger_delivery_destination()) {
                    dropBattery((Battery) target.get(), timeLapse);
                } else if (!has_delivering_battery() && is_in_charger_delivery_destination()) {
                    updateBattery(timeLapse);
                    target = Optional.absent();
                } else if (this.agvModel.containerContains(this, target.get())) {
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

    private void dropBattery(Battery battery, TimeLapse timeLapse) {
        this.agvModel.drop(this, battery, timeLapse);
        this.agvModel.unregister(battery);
    }

    public void updateBattery(TimeLapse timeLapse) {
        Point position = this.getRoadModel().getPosition(this);
        Battery newbattery = new Battery(position);
        this.agvModel.registerBattery(newbattery);
        this.agvModel.pickup(this, newbattery, timeLapse);
    }

    public boolean is_in_charger_delivery_destination() {
        Battery battery = (Battery) this.target.get();
        return this.getRoadModel().getPosition(this).equals(battery.destination);
    }

    public boolean is_in_delivery_destination() {
        return this.getRoadModel().getPosition(this).equals(target.get().getDeliveryLocation());
    }

    public boolean is_in_pick_destination() {
        return this.getRoadModel().getPosition(this).equals(target.get().getPickupLocation());
    }

    public boolean is_delivering_box() {
        if (this.target.get().getDeliveryDuration() == CHARGING_DURATION) {
            return false;
        }
        return true;
    }

    public boolean has_delivering_box() {
        return this.agvModel.containerContains(this, target.get());
    }

    public boolean has_delivering_battery() {
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

    public BatteryCharger getBatteryCharger(Point position) {
        BatteryCharger charger = null;
        Set<BatteryCharger> chargerers = this.getRoadModel().getObjectsOfType(BatteryCharger.class);
        for (BatteryCharger charg : chargerers) {
            if (charg.position.equals(position)) {
                charger = charg;
            }
        }
        return charger;
    }
}
