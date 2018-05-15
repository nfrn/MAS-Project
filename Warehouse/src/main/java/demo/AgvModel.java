package demo;

import com.github.rinde.rinsim.core.SimulatorAPI;
import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.*;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.auto.value.AutoValue;
import demo.AutoValue_AgvModel_Builder;
import org.apache.commons.math3.random.RandomGenerator;

import javax.annotation.CheckReturnValue;
import java.util.List;

/**
 * @author Matija Kljun
 */
public class AgvModel extends ForwardingPDPModel implements SimulatorUser, RoadUser {

    private List<Point> depot_locations;
    //final RandomGenerator rng;
    private SimulatorAPI simulator;
    private RoadModel roadModel;

    protected AgvModel(PDPModel deleg) {
        super(deleg);
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


/*
    @Override
    public void deliver(Vehicle vehicle, Parcel parcel, TimeLapse time) {
        //ForwardingPDPModel.builder();
    }
*/

    @Override
    public void setSimulator(SimulatorAPI simulatorAPI) {
        this.simulator = simulatorAPI;
    }

    @Override
    public void initRoadUser(RoadModel roadModel) {
        this.roadModel = roadModel;
    }

    static Builder builder() {
        return Builder.create();
    }

    @AutoValue
    public abstract static class Builder extends ModelBuilder.AbstractModelBuilder<AgvModel, PDPObject> {
        private static final long serialVersionUID = 165944940216903075L;

        Builder() {
            //this.setProvidingTypes(new Class[]{DefaultPDPModel.class});
            this.setDependencies(new Class[]{RoadModel.class});
        }

        public AgvModel build(DependencyProvider dependencyProvider) {
            DefaultPDPModel deleg = DefaultPDPModel.builder().build(dependencyProvider);
            return new AgvModel(deleg);
        }

        static Builder create() {
            return new AutoValue_AgvModel_Builder();
        }
    }

}


