package DelgMas;

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
import DelgMas.AutoValue_AgvModel_Builder;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;


public class AgvModel extends ForwardingPDPModel implements SimulatorUser, RoadUser {

    private List<Point> depot_locations;
    private SimulatorAPI simulator;
    private RoadModel roadModel;

    protected AgvModel(PDPModel deleg) {
        super(deleg);
        depot_locations = new ArrayList<>();
        for (int i = 0; i < AgvExample.NUM_DEPOTS; ++i)
            depot_locations.add(new Point(76, i * 12));
    }

    static Builder builder() {
        return Builder.create();
    }

    void store_box(AgvAgent agv, Box box, TimeLapse timeLapse, RandomGenerator rng) {
        Point loc = box.getDeliveryLocation();
        this.deliver(agv, box, timeLapse);
        if (box.finaldestination) {
            System.out.println("Final destination");
            return;
        } else {
            simulator.register(new Box(Parcel.builder(
                    loc,
                    depot_locations.get(rng.nextInt(AgvExample.NUM_DEPOTS)))
                    .neededCapacity(AgvExample.MAX_CAPACITY)
                    .pickupTimeWindow(TimeWindow.create(2, 4))
                    .deliveryTimeWindow(TimeWindow.create(4, 6))
                    .pickupDuration(AgvExample.SERVICE_DURATION)
                    .buildDTO(), true));
        }
    }

    public RoadModel getRoadModel() {
        return roadModel;
    }

    @Override
    public void setSimulator(SimulatorAPI simulatorAPI) {
        this.simulator = simulatorAPI;
    }

    @Override
    public void initRoadUser(RoadModel roadModel) {
        this.roadModel = roadModel;
    }

    @AutoValue
    public abstract static class Builder extends ModelBuilder.AbstractModelBuilder<AgvModel, PDPObject> {
        private static final long serialVersionUID = 165944940216903075L;

        Builder() {
            setDependencies(RoadModel.class);
            setProvidingTypes(AgvModel.class);
        }

        static Builder create() {
            return new AutoValue_AgvModel_Builder();
        }

        public AgvModel build(DependencyProvider dependencyProvider) {
            DefaultPDPModel deleg = DefaultPDPModel.builder().build(dependencyProvider);
            return new AgvModel(deleg);
        }
    }

}