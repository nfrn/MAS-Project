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

    private final RandomGenerator rng;
    private Optional<Box> target;
    private Queue<Point> path;
    //private Optional<AgvModel> agvModel;

    AgvAgent(Point startPosition, RandomGenerator r) {
        super(VehicleDTO.builder()
                .capacity(CAPACITY)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        rng = r;
        target = Optional.absent();
        path = new LinkedList<>();
        //this.agvModel = Optional.absent();
    }

    void nextDestination() {
        if (this.getPDPModel().getVehicleState(this).equals(PDPModel.VehicleState.IDLE)) {
            // find parcel and go to it
            Collection<Parcel> parcels = this.getPDPModel().getParcels(PDPModel.ParcelState.AVAILABLE);

            Optional<Box> curr = Optional.fromNullable(RoadModels.findClosestObject(
                    this.getRoadModel().getPosition(this), this.getRoadModel(), Box.class));

            if (curr.isPresent())
                this.target = Optional.of(curr.get());
        }
    }

    void pickup(Point loc, TimeLapse tm) {
        Iterator<Box> boxes = this.getRoadModel().getObjectsAt(this, Box.class).iterator();


        this.getPDPModel().pickup(this, boxes.next(), tm);
    }

    void move(TimeLapse tm) {
        if(this.getPDPModel().getContents(this).contains(target.get()))
            this.getRoadModel().moveTo(this, this.target.get().getDeliveryLocation(), tm);
        else
            this.getRoadModel().moveTo(this, this.target.get().getPickupLocation(), tm);
    }

    /*void registerAgvModel(AgvModel agvModel) {
        this.agvModel = Optional.of(agvModel);
    }*/

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {

    }

    @Override
    protected void tickImpl(TimeLapse timeLapse) {
        if (!timeLapse.hasTimeLeft()) {
            return;
        }

        if (!target.isPresent())
            this.nextDestination();

        if(target.isPresent()) {
            if (!this.getPDPModel().containerContains(this, target.get()) &&
                    this.getRoadModel().getPosition(this).equals(target.get().getPickupLocation())) {
                pickup(this.getRoadModel().getPosition(this), timeLapse);
            } else if (this.getPDPModel().containerContains(this, target.get()) &&
                    this.getRoadModel().getPosition(this).equals(target.get().getDeliveryLocation())) {
                getPDPModel().deliver(this, target.get(), timeLapse);
                //this.agvModel.get().store_box(getPDPModel(), this, target.get(), timeLapse);
                target = Optional.absent();
                Set<RoadUser> users = this.getRoadModel().getObjects();
                users.size();
            } else if (this.getPDPModel().containerContains(this, target.get())) {
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
