package demo;

import com.github.rinde.rinsim.core.SimulatorAPI;
import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.model.Model;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.List;

/**
 * @author Matija Kljun
 */
public class AgvModel extends Model.AbstractModel<AgvAgent> implements SimulatorUser, RoadUser {

    private List<Point> depot_locations;
    //final RandomGenerator rng;
    private SimulatorAPI simulator;
    private RoadModel roadModel;

    AgvModel(List<Point> depot_locations) {

        this.depot_locations = depot_locations;
    }

    void store_box(PDPModel model, AgvAgent agv, Box box, TimeLapse timeLapse, RandomGenerator r) {
        Point loc = box.getDeliveryLocation();
        model.deliver(agv, box, timeLapse);
        simulator.register(new Box(Parcel.builder(
                loc,
                depot_locations.get(r.nextInt(AgvExample.NUM_DEPOTS)))
                .neededCapacity(AgvExample.MAX_CAPACITY)
                .pickupTimeWindow(TimeWindow.create(2,4))
                .deliveryTimeWindow(TimeWindow.create(4,6))
                .pickupDuration(AgvExample.SERVICE_DURATION)
                .buildDTO()));
    }

    @Override
    public boolean register(AgvAgent element) {
        //element.registerAgvModel(this);
        return true;
    }

    @Override
    public boolean unregister(AgvAgent element) {
        return false;
    }

    @Override
    public void setSimulator(SimulatorAPI simulatorAPI) {
        this.simulator = simulatorAPI;
    }

    @Override
    public void initRoadUser(RoadModel roadModel) {
        this.roadModel = roadModel;
    }
}
