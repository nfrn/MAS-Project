//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package TaskAgent;
import ResourceAgent.Customer;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.google.common.base.Optional;

/**
 * Implementation of a very simple taxi agent. It moves to the closest customer,
 * picks it up, then delivers it, repeat.
 *
 * @author Rinde van Lon
 */
public class Taxi extends Vehicle{
    private static final double SPEED = 1000d;
    private static final int FUELCAPACITY = 100;

    private Optional<Parcel> curr;
    /*Extra*/
    private int current_fuel;

    private Taxi_DMAS delegate_ant_mas;

    public Taxi(Point startPosition, int capacity) {
        super(VehicleDTO.builder()
                .capacity(capacity)
                .startPosition(startPosition)
                .speed(SPEED)
                .build());
        curr = Optional.absent();
        current_fuel = FUELCAPACITY;

        delegate_ant_mas = new Taxi_DMAS();
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {}

    @Override
    protected void tickImpl(TimeLapse time) {
        final RoadModel rm = getRoadModel();
        final PDPModel pm = getPDPModel();

        if (!time.hasTimeLeft()) {
            return;
        }
        if (!curr.isPresent()) {



            curr = Optional.fromNullable(RoadModels.findClosestObject(
                    rm.getPosition(this), rm, Parcel.class));
        }
        if (curr.isPresent()) {
            final boolean inCargo = pm.containerContains(this, curr.get());
            // sanity check: if it is not in our cargo AND it is also not on the
            // RoadModel, we cannot go to curr anymore.
            if (!inCargo && !rm.containsObject(curr.get())) {
                curr = Optional.absent();
            } else if (inCargo) {
                // if it is in cargo, go to its destination
                rm.moveTo(this, curr.get().getDeliveryLocation(), time);
                if (rm.getPosition(this).equals(curr.get().getDeliveryLocation())) {
                    // deliver when we arrive
                    Customer cust = (Customer)curr.get();
                    cust.remove();
                    pm.deliver(this, curr.get(), time);

                }
            } else {
                // it is still available, go there as fast as possible
                rm.moveTo(this, curr.get(), time);
                if (rm.equalPosition(this, curr.get())) {
                    // pickup customer
                    pm.pickup(this, curr.get(), time);
                }
            }

        }
    }
}