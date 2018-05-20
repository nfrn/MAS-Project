package DelgMas;

import com.github.rinde.rinsim.core.SimulatorAPI;
import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.*;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.core.model.time.TimeModel;
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

    @Override
    public TimeWindowPolicy getTimeWindowPolicy(){
        return TimeWindowPolicy.TimeWindowPolicies.STRICT;
    }
    static Builder builder() {
        return Builder.create();
    }

    void store_box(AgvAgent agv, Box box, TimeLapse timeLapse, RandomGenerator rng) {
        Point loc = box.getDeliveryLocation();
        this.deliver(agv, box, timeLapse);
        if (box.finaldestination) {
            return;
        } else {
            long currentTime = timeLapse.getTime();
            simulator.register(new Box(loc,
                    depot_locations.get(rng.nextInt(AgvExample.NUM_DEPOTS)), currentTime , true));
        }
    }

    public void registerBattery(Battery battery){
        this.simulator.register(battery);
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