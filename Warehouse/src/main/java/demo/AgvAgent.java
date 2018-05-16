package demo;

import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.*;

class AgvAgent extends Vehicle implements TickListener, RoadUser {
    private static final double SPEED = 1;
    private static final int CAPACITY = 1;
    public static final int POWERLIMIT = 500;
    private static final double POWERCONSUME = 0.1;

    private final RandomGenerator rng;
    private Optional<Box> target;
    private Queue<Point> path;
    public int power;
    private AgvModel agvModel;

    AgvAgent(Point startPosition, RandomGenerator r, AgvModel agv) {
        super(VehicleDTO.builder()
                .capacity(CAPACITY)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        rng = r;
        target = Optional.absent();
        path = new LinkedList<>();
        power = POWERLIMIT;
        this.agvModel = agv;
    }

    void nextDestination() {
        if (this.agvModel.getVehicleState(this).equals(PDPModel.VehicleState.IDLE)) {
            // find parcel and go to it
            Collection<Parcel> parcels = this.agvModel.getParcels(PDPModel.ParcelState.AVAILABLE);

            Optional<Box> curr = Optional.fromNullable(RoadModels.findClosestObject(
                    this.getRoadModel().getPosition(this), this.getRoadModel(), Box.class));

            if (curr.isPresent())
                this.target = Optional.of(curr.get());
        }
    }

    void pickup(Point loc, TimeLapse tm) {
        Iterator<Box> boxes = this.getRoadModel().getObjectsAt(this, Box.class).iterator();


        this.agvModel.pickup(this, boxes.next(), tm);
    }

    void move(TimeLapse tm) {
        if(this.agvModel.getContents(this).contains(target.get()))
            this.getRoadModel().moveTo(this, this.target.get().getDeliveryLocation(), tm);
        else
            this.getRoadModel().moveTo(this, this.target.get().getPickupLocation(), tm);
        this.power -= (POWERCONSUME*tm.getTickLength())/1000;
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {

    }

    @Override
    protected void tickImpl(TimeLapse timeLapse) {
        if (this.power<=0){
            return;
        }
        if (!timeLapse.hasTimeLeft()) {
            return;
        }
        if (!target.isPresent())
            this.nextDestination();

        if(target.isPresent()) {
            if (!this.agvModel.containerContains(this, target.get()) &&
                    this.getRoadModel().getPosition(this).equals(target.get().getPickupLocation())) {
                pickup(this.getRoadModel().getPosition(this), timeLapse);
            } else if (this.agvModel.containerContains(this, target.get()) &&
                    this.getRoadModel().getPosition(this).equals(target.get().getDeliveryLocation())) {
                //getPDPModel().deliver(this, target.get(), timeLapse);
                this.agvModel.store_box(this, target.get(), timeLapse,rng);
                target = Optional.absent();
                Set<RoadUser> users = this.getRoadModel().getObjects();
                users.size();
            } else if (this.agvModel.containerContains(this, target.get())) {
                move(timeLapse);
            } else {
                if (getRoadModel().containsObject(target.get())) {
                    move(timeLapse);
                } else {
                    target = Optional.absent();
                }
            }
        }
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

}
