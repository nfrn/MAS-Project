package DelgMas;

import Ui.RoadDataPanel;
import Ui.StringListener;
import Ui.TaskListener;
import Ui.TasksPanel;
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

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static DelgMas.AgvExample.NUM_AGVS;
import static DelgMas.AgvExample.NUM_BOXES;
import static DelgMas.Battery.CHARGING_DURATION;


public class AgvModel extends ForwardingPDPModel implements SimulatorUser, RoadUser {

    private List<Point> depot_locations;
    private SimulatorAPI simulator;
    private RoadModel roadModel;
    public TaskListener taskListener;

    protected AgvModel(PDPModel deleg) {
        super(deleg);
        depot_locations = new ArrayList<>();
        for (int i = 0; i < AgvExample.NUM_DEPOTS; ++i)
            depot_locations.add(new Point(76, i * 12));
        createUi(this);
    }

    static Builder builder() {
        return Builder.create();
    }

    void store_box(AgvAgent agv, Box box, TimeLapse timeLapse, RandomGenerator rng) {
        Point loc = box.getDeliveryLocation();
        this.deliver(agv, box, timeLapse);
        this.unregister(box);
        if (box.finaldestination) {
            return;
        } else {//if (roadModel.getObjectsOfType(Parcel.class).size() <= NUM_BOXES + NUM_AGVS) {
            long currentTime = timeLapse.getTime();
            Box newBox = new Box(loc,
                    depot_locations.get(rng.nextInt(AgvExample.NUM_DEPOTS)), currentTime, true, rng.nextInt(Box.MAX_STORAGE_TIME));
            //newBox.
            simulator.register(newBox);
        }
    }

    public void registerBattery(Battery battery) {
        this.simulator.register(battery);
    }

    public RoadModel getRoadModel() {
        return roadModel;
    }

    public List<Box> getAvailableBoxes() {
        List<Box> boxes = new ArrayList<>(this.roadModel.getObjectsOfType(Box.class));
        List<Box> availableBoxes = new ArrayList<>();
        for (Box b : boxes) {
            if (b.isAvailable)
                availableBoxes.add(b);
        }

        return availableBoxes;
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
            setProvidingTypes(AgvModel.class,PDPModel.class);
        }

        static Builder create() {
            return new AutoValue_AgvModel_Builder();
        }

        public AgvModel build(DependencyProvider dependencyProvider) {
            DefaultPDPModel deleg = DefaultPDPModel.builder().build(dependencyProvider);
            return new AgvModel(deleg);
        }
    }


    public void createUi(final AgvModel agvModel) {
        getBoxes();
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                TasksPanel panel = new TasksPanel(agvModel, agvModel.getBoxes());
                panel.setVisible(true);
            }
        });
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public synchronized ArrayList<Box> getBoxes() {
        ArrayList<Box> output = new ArrayList<Box>();
        for (Parcel parcel : this.getParcels(ParcelState.values())) {
            if (parcel.getDeliveryDuration() != CHARGING_DURATION) {
                output.add((Box) parcel);
            }
        }
        return output;
    }
}