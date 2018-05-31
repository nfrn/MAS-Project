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

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Length;
import javax.measure.quantity.Velocity;
import javax.measure.unit.BaseUnit;
import java.util.*;

import static DelgMas.Battery.CHARGING_DURATION;

public class AgvAgent extends Vehicle implements TickListener, RoadUser {
    public static final double SPEED = 1;
    private static final int CAPACITY = 2;
    private static final double POWERCONSUME = 1;
    public static final long VISIT_TIME_LENGTH = 1500;
    public static final int INTERVAL_BEG = 0;
    public static final long INTERVAL_MAX = 30;
    public static final long INTERVAL_INC = 1;

    public static final int SLEEP_DURATION = 20;

    public int ID;

    private RandomGenerator rng;
    private AgvModel agvModel;
    private DMASModel dmasModel;

    public boolean hasBattery;
    private Optional<Parcel> target;
    private Queue<Point> path;

    private int sleep;

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

    private Box reasonAboutBoxes(final Point position, final long time, PheromoneStorage store) {
        ArrayList<Parcel> boxes_info = new ArrayList<>(store.list_phero_C.get(0).boxes_info);
        boxes_info.sort(new Comparator<Parcel>() {
            @Override
            public int compare(Parcel b1, Parcel b2) {
                Box b_1 = (Box) b1;
                Box b_2 = (Box) b2;
//                System.out.println(b_1.finaldestination);
//                System.out.println(b_2.finaldestination);
                if (!b_1.isAvailable) {
                    return -1;
                }
                if (!b_2.isAvailable) {
                    return 1;
                }

                if (b_1.finaldestination && !b_2.finaldestination) {
                    return 1;
                }

                if (!b_1.finaldestination && b_2.finaldestination) {
                    return -1;
                }

                double pickupdistance1 = Math.sqrt(Math.pow(b1.getPickupLocation().x - position.x, 2) + Math.pow(b1.getPickupLocation().y - position.y, 2));
                double pickupdistance2 = Math.sqrt(Math.pow(b2.getPickupLocation().x - position.x, 2) + Math.pow(b2.getPickupLocation().y - position.y, 2));
                double deliverydistance1 = Math.sqrt(Math.pow(b1.getDeliveryLocation().x - position.x, 2) + Math.pow(b1.getDeliveryLocation().y - position.y, 2));
                double deliverydistance2 = Math.sqrt(Math.pow(b2.getDeliveryLocation().x - position.x, 2) + Math.pow(b2.getDeliveryLocation().y - position.y, 2));

                double pickuptimeleft1 = b1.getPickupTimeWindow().end() - time;
                double pickuptimeleft2 = b2.getPickupTimeWindow().end() - time;
                double deliverytimeleft1 = b1.getDeliveryTimeWindow().end() - time;
                double deliverytimeleft2 = b2.getDeliveryTimeWindow().end() - time;

                double score1 = pickupdistance1 * pickuptimeleft1 + deliverydistance1 * deliverytimeleft1;
                double score2 = pickupdistance2 * pickuptimeleft2 + deliverydistance2 * deliverytimeleft2;

                double difference = score1 - score2;
                if (difference > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        if (boxes_info.size() == 0)
            return null;
        else
            return (Box) boxes_info.get(boxes_info.size() - 1);
    }

    private boolean canwegetthere(Point p1, Point p2, Point p3) {
        double dist = Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
        double dist2 = Math.abs(p2.x - p3.x) + Math.abs(p2.y - p3.y);

        Measure<Double, Velocity> speed = Measure.valueOf(this.getSpeed(), this.getRoadModel().getSpeedUnit());
        BaseUnit<Duration> duration = new BaseUnit<Duration>("s");

        Measure<Double, Length> distance = Measure.valueOf(dist, this.getRoadModel().getDistanceUnit());
        Measure<Double, Length> distance2 = Measure.valueOf(dist2, this.getRoadModel().getDistanceUnit());

        double travelTime = RoadModels.computeTravelTime(speed, distance, duration);
        travelTime += RoadModels.computeTravelTime(speed, distance2, duration);

//        System.out.println(travelTime);
//        System.out.println(getBattery().capacity);
        if (getBattery().capacity <= travelTime) {

//            System.out.println("False");
            return false;
        }
        return true;
    }

    private void nextDestination(TimeLapse timeLapse) {
        if (this.agvModel.getVehicleState(this).equals(PDPModel.VehicleState.IDLE)) {
            Point position = this.getRoadModel().getPosition(this);
            Box box = reasonAboutBoxes(position, timeLapse.getTime(), dmasModel.nodes.get(dmasModel.getClosestNode(position)));

            if(box == null)
                return;

            Optional<Box> curr = Optional.fromNullable(box);

            if (curr.isPresent())
                this.target = Optional.of((Parcel) curr.get());

            if (!canwegetthere(position, curr.get().getPickupLocation(), curr.get().getDeliveryLocation())) {
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

    private boolean moveBattery(TimeLapse tm) {
        Battery battery = (Battery) this.target.get();
        BatteryCharger charger = getBatteryCharger(battery.destination);

        Point currPoint = this.getRoadModel().getPosition(this);
        Point nearestPoint = currPoint;
        if (!this.dmasModel.graphRoadModel.getGraph().containsNode(currPoint)) {
            System.out.println("not on a node!!!!!!!");
            Connection c = this.dmasModel.graphRoadModel.getConnection(this).get();
            nearestPoint = c.to();
        }

        boolean goSleep = false;

        int result = -1;
        List<TimeWindow> tws = new ArrayList<>();
        Queue<Point> queue = new LinkedList<>();
        int i = 0;
        int interval = INTERVAL_BEG;
        while (result == -1) {
            System.out.println("searching path...");
            queue = new LinkedList<>();
            queue.add(currPoint);
            if (!currPoint.equals(nearestPoint))
                queue.add(nearestPoint);
            queue.addAll(dmasModel.releaseAnts_D(nearestPoint, battery.destination, interval));

            // add charging time to timewindows
            tws = getTimeWindowsForPath(new ArrayList<Point>(queue), tm);
            TimeWindow lastTw = tws.get(tws.size() - 1);
            TimeWindow newTw = TimeWindow.create(lastTw.begin(), lastTw.end() + battery.getDeliveryDuration());
            tws.set(tws.size() - 1, newTw);

            result = dmasModel.releaseAnts_CheckBooking(queue, tws, this.ID, this);
            i++;
            if (interval < INTERVAL_MAX) {
                interval += INTERVAL_INC;
            } else {
                goSleep = true;
                this.currentPath = Optional.absent();
                break;
            }
        }
        if (!goSleep) {
            // check if battery charger is free ???

//            TimeWindow lastTw = tws.get(tws.size() - 1);
//            TimeWindow newTw = TimeWindow.create(lastTw.begin(), lastTw.end() + battery.getDeliveryDuration());
//            tws.set(tws.size() - 1, newTw);

            dmasModel.releaseAnts_Booking(queue, tws, this.ID, this);
            this.currentPath = Optional.of(queue);

            charger.bookBatteryCharger(tws.get(tws.size() - 1));

            this.getRoadModel().followPath(this, queue, tm);
            this.getBattery().capacity -= POWERCONSUME;
            return true;
            //} else
            //    return false;
        } else {
            return false;
        }

    }

    private boolean moveBox(TimeLapse tm) {
        boolean goSleep = false;
        Point dest;
        if (this.agvModel.getContents(this).contains(target.get())) {
            dest = this.target.get().getDeliveryLocation();
        } else {
            dest = this.target.get().getPickupLocation();
        }

//        if(this.ID==0) System.out.println("processing");
        Queue<Point> queue = new LinkedList<>();
        List<TimeWindow> tws = new ArrayList<>();

        boolean getNewPath = false;
        if (this.currentPath.isPresent() && !this.currentPath.get().isEmpty()) {

            Point currPoint = this.getRoadModel().getPosition(this);
//            Point nearestPoint = currPoint;
            queue = new LinkedList<>();
            if (!this.dmasModel.graphRoadModel.getGraph().containsNode(currPoint)) {
                System.out.println("saved path not on a node!!! " + this.ID);
//                Connection c = this.dmasModel.graphRoadModel.getConnection(this).get();
//                Point nearestPoint = c.to();
                queue.add(currPoint);
            } else if (!((LinkedList) this.currentPath.get()).getFirst().equals(currPoint)) {
                queue.add(currPoint);
            }

            queue.addAll(this.currentPath.get());

            tws = getTimeWindowsForPath(new ArrayList<Point>(queue), tm);
            int result = dmasModel.releaseAnts_CheckBooking(queue, tws, this.ID, this);
            if (result == -1)
                getNewPath = true;
            else {
                // rebook the old path
                dmasModel.releaseAnts_Booking(queue, tws, this.ID, this);
                this.currentPath = Optional.of(queue);
            }

        } else
            getNewPath = true;

        if (getNewPath) {
            //List<Point> shortestPathTo = this.getRoadModel().getShortestPathTo(this, dest);
            Point currPoint = this.getRoadModel().getPosition(this);
            Point nearestPoint = currPoint;
            if (!this.dmasModel.graphRoadModel.getGraph().containsNode(currPoint)) {
                System.out.println("getNewPath not on a node!!! " + this.ID);
                Connection c = this.dmasModel.graphRoadModel.getConnection(this).get();
                nearestPoint = c.to();
            }

            int result = -1;
            tws = new ArrayList<>();
            queue = new LinkedList<>();

            int i = 0;
            int interval = INTERVAL_BEG;
            while (result == -1) {
//                System.out.println("searching path...");
                queue = new LinkedList<>();
                queue.add(currPoint);
                if (!currPoint.equals(nearestPoint))
                    queue.add(nearestPoint);

                queue.addAll(dmasModel.releaseAnts_D(nearestPoint, dest, interval));
                tws = getTimeWindowsForPath(new ArrayList<Point>(queue), tm);
                result = dmasModel.releaseAnts_CheckBooking(queue, tws, this.ID, this);
                i++;
                if (interval < INTERVAL_MAX) {
                    interval += INTERVAL_INC;
                } else {
                    goSleep = true;
                    this.currentPath = Optional.absent();
                    break;
                }
            }
            if (!goSleep) {
                dmasModel.releaseAnts_Booking(queue, tws, this.ID, this);
                this.currentPath = Optional.of(queue);
            }
        }

        if (!goSleep) {
            System.out.println(queue);
            try {
                this.getRoadModel().followPath(this, this.currentPath.get(), tm);
            } catch (Exception e) {
                System.out.println("errr");
            }

            this.getBattery().capacity -= POWERCONSUME;
            return true;
        } else
            return false;
    }

    private List<TimeWindow> getTimeWindowsForPath(List<Point> path, TimeLapse tm) {

        List<TimeWindow> timeWindows = new ArrayList<>();

        long addTime = tm.getTime();
        //if (this.dmasModel.graphRoadModel.getGraph().containsNode(path.get(0)))
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

    public void bookCurrentPosition(TimeLapse tm) {
        Point currPoint = this.getRoadModel().getPosition(this);
        if (this.dmasModel.graphRoadModel.getGraph().containsNode(currPoint)) {
            // book just the node
            Queue<Point> q = new LinkedList<>();
            q.add(currPoint);
            List<TimeWindow> tws = new ArrayList<>();
            tws.add(TimeWindow.create(tm.getTime(), tm.getTime() + VISIT_TIME_LENGTH));
            this.dmasModel.releaseAnts_Booking(q, tws, this.ID, this);
        } else {
            // book the connection
            if(this.ID == 2)
                System.out.println();

            System.out.println("current not on a node!!! " + this.ID);
            Connection c = this.dmasModel.graphRoadModel.getConnection(this).get();
            Point a = c.from();
            Point b = c.to();
            Queue<Point> q = new LinkedList<>();
            q.add(a);
            q.add(b);
            List<TimeWindow> tws = new ArrayList<>();
            tws.add(TimeWindow.create(Math.max(0, tm.getTime() - VISIT_TIME_LENGTH), Math.max(0, tm.getTime() - VISIT_TIME_LENGTH)));
            tws.add(TimeWindow.create(tm.getTime() + VISIT_TIME_LENGTH, tm.getTime() + 2 * VISIT_TIME_LENGTH));
            this.dmasModel.releaseAnts_Booking(q, tws, this.ID, this);
        }
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {

    }

    @Override
    protected void tickImpl(TimeLapse timeLapse) {
        //First task is every agent to load the battery they have.
        //Battery battery = getBattery();
        if(this.ID == 2)
            System.out.println();
        if(this.ID == 5)
            System.out.println();

        this.bookCurrentPosition(timeLapse);

        if (!this.hasBattery) {
            this.hasBattery = true;
            pickupBattery(timeLapse);
            dmasModel.releaseAnts_Boxs_Info();
            return;
        }

        if (!timeLapse.hasTimeLeft()) {
            return;
        }
        if (!target.isPresent()) {
            System.out.println(this.ID +" no box ???????????????");
            this.nextDestination(timeLapse);
        }
        if (target.isPresent()) {
            if (is_delivering_box()) {
                if (!has_delivering_box() && is_in_pick_destination()) {
                    pickupBox(timeLapse);
                } else if (has_delivering_box() && is_in_delivery_destination()) {
                    this.agvModel.store_box(this, (Box) target.get(), timeLapse, rng);
                    this.currentPath = Optional.absent();
                    target = Optional.absent();
                } else if (this.agvModel.containerContains(this, target.get())) {
                    boolean success = moveBox(timeLapse);
                    if (!success)
                        this.sleep = SLEEP_DURATION;
                } else {
                    if (getRoadModel().containsObject(target.get())) {
                        boolean success = moveBox(timeLapse);
                        if (!success)
                            this.sleep = SLEEP_DURATION;
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
                    boolean success = moveBattery(timeLapse);
                    if (!success)
                        this.sleep = SLEEP_DURATION;
                } else {
                    if (getRoadModel().containsObject(target.get())) {
                        boolean success = moveBattery(timeLapse);
                        if (!success)
                            this.sleep = SLEEP_DURATION;
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
