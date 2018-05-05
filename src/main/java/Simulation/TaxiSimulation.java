//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Simulation;

import static com.google.common.collect.Maps.newHashMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;

import ResourceAgent.Customer;
import ResourceAgent.Feasibility_Ant;
import TaskAgent.Exploration_Ant;
import TaskAgent.Intention_Ant;
import TaskAgent.Taxi;
import TaskAgent.TaxiBase;
import org.apache.commons.math3.random.RandomGenerator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.DefaultPDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.event.Listener;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.MultiAttributeData;
import com.github.rinde.rinsim.geom.io.DotGraphIO;
import com.github.rinde.rinsim.geom.io.Filters;
import Ui.View;
import Ui.renderers.GraphRoadModelRenderer;
import Ui.renderers.RoadUserRenderer;

import Simulation.TaxiRenderer.Language;


/**
 * Example showing a fleet of taxis that have to pickup and transport customers
 * around the city of Leuven.
 * <p>
 * If this class is run on MacOS it might be necessary to use
 * -XstartOnFirstThread as a VM argument.
 * @author Rinde van Lon
 */
public final class TaxiSimulation {

    private static final int NUM_DEPOTS = 1;
    private static final int NUM_TAXIS = 20;
    private static final int NUM_CUSTOMERS = 30;

    // time in ms
    private static final long SERVICE_DURATION = 60000;
    private static final int TAXI_CAPACITY = 10;
    private static final int DEPOT_CAPACITY = 100;

    private static final int SPEED_UP = 4;
    private static final int MAX_CAPACITY = 3;
    private static final double NEW_CUSTOMER_PROB = .007;

    private static final String MAP_FILE = "/data/maps/leuven-simple.dot";
    private static final Map<String, Graph<MultiAttributeData>> GRAPH_CACHE =
            newHashMap();

    private static final long TEST_STOP_TIME = 20 * 60 * 1000;
    private static final int TEST_SPEED_UP = 64;

    private TaxiSimulation() {}

    /**
     * Starts the {@link TaxiSimulation}.
     * @param args The first option may optionally indicate the end time of the
     *          simulation.
     */
    public static void main(@Nullable String[] args) {
        final long endTime = args != null && args.length >= 1 ? Long
                .parseLong(args[0]) : Long.MAX_VALUE;

        final String graphFile = args != null && args.length >= 2 ? args[1]
                : MAP_FILE;
        run(false, endTime, graphFile, null /* new Display() */, null, null);
    }

    /**
     * Run the example.
     * @param testing If <code>true</code> enables the test mode.
     */
    public static void run(boolean testing) {
        run(testing, Long.MAX_VALUE, MAP_FILE, null, null, null);
    }

    /**
     * Starts the example.
     * @param testing Indicates whether the method should run in testing mode.
     * @param endTime The time at which simulation should stop.
     * @param graphFile The graph that should be loaded.
     * @param display The display that should be used to show the Ui on.
     * @param m The monitor that should be used to show the Ui on.
     * @param list A listener that will receive callbacks from the Ui.
     * @return The simulator instance.
     */
    public static Simulator run(boolean testing, final long endTime,
                                String graphFile,
                                @Nullable Display display, @Nullable Monitor m, @Nullable Listener list) {

        final View.Builder view = createGui(testing, display, m, list);

        // use map of leuven
        final Simulator simulator = Simulator.builder()
                .addModel(RoadModelBuilders.staticGraph(loadGraph(graphFile)))
                .addModel(DefaultPDPModel.builder())
                .addModel(view)
                .build();
        final RandomGenerator rng = simulator.getRandomGenerator();

        final RoadModel roadModel = simulator.getModelProvider().getModel(
                RoadModel.class);


        // add depots, taxis and parcels to simulator
        for (int i = 0; i < NUM_DEPOTS; i++) {
            simulator.register(new TaxiBase(roadModel.getRandomPosition(rng),
                    DEPOT_CAPACITY));
            simulator.register(new Intention_Ant(roadModel.getRandomPosition(rng)));
            simulator.register(new Exploration_Ant(roadModel.getRandomPosition(rng)));
            simulator.register(new Feasibility_Ant(roadModel.getRandomPosition(rng)));
        }
        for (int i = 0; i < NUM_TAXIS; i++) {
            simulator.register(new Taxi(roadModel.getRandomPosition(rng),
                    TAXI_CAPACITY));
        }
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            simulator.register(new Customer(
                    Parcel.builder(roadModel.getRandomPosition(rng),
                            roadModel.getRandomPosition(rng))
                            .serviceDuration(SERVICE_DURATION)
                            .neededCapacity(1 + rng.nextInt(MAX_CAPACITY))
                            .buildDTO()));
        }

        simulator.addTickListener(new TickListener() {
            @Override
            public void tick(TimeLapse time) {
                if (time.getStartTime() > endTime) {
                    simulator.stop();
                } else if (rng.nextDouble() < NEW_CUSTOMER_PROB) {
                    simulator.register(new Customer(
                            Parcel
                                    .builder(roadModel.getRandomPosition(rng),
                                            roadModel.getRandomPosition(rng))
                                    .serviceDuration(SERVICE_DURATION)
                                    .neededCapacity(1 + rng.nextInt(MAX_CAPACITY))
                                    .buildDTO()));
                }
            }

            @Override
            public void afterTick(TimeLapse timeLapse) {}
        });
        simulator.start();

        return simulator;
    }

    static View.Builder createGui(
            boolean testing,
            @Nullable Display display,
            @Nullable Monitor m,
            @Nullable Listener list) {

        View.Builder view = View.builder()
                .with(GraphRoadModelRenderer.builder())
                .with(RoadUserRenderer.builder()
                        .withImageAssociation(
                                TaxiBase.class, "/graphics/perspective/tall-building-32.png")
                        .withImageAssociation(
                                Taxi.class, "/graphics/flat/taxi-32.png")
                        .withImageAssociation(
                                Customer.class, "/graphics/flat/person-red-32.png")
                        .withImageAssociation(
                                Feasibility_Ant.class, "/graphics/flat/Ants/exploration.png")
                        .withImageAssociation(
                                Exploration_Ant.class, "/graphics/flat/Ants/feasibility.png")
                        .withImageAssociation(
                                Intention_Ant.class, "/graphics/flat/Ants/intention.png"))
                .with(TaxiRenderer.builder(Language.ENGLISH))
                .withTitleAppendix("TaskAgent example");

        if (testing) {
            view = view.withAutoClose()
                    .withAutoPlay()
                    .withSimulatorEndTime(TEST_STOP_TIME)
                    .withSpeedUp(TEST_SPEED_UP);
        } else if (m != null && list != null && display != null) {
            view = view.withMonitor(m)
                    .withSpeedUp(SPEED_UP)
                    .withResolution(m.getClientArea().width, m.getClientArea().height)
                    .withDisplay(display)
                    .withCallback(list)
                    .withAsync()
                    .withAutoPlay()
                    .withAutoClose();
        }
        return view;
    }

    // load the graph file
    static Graph<MultiAttributeData> loadGraph(String name) {
        try {
            if (GRAPH_CACHE.containsKey(name)) {
                return GRAPH_CACHE.get(name);
            }
            final Graph<MultiAttributeData> g = DotGraphIO
                    .getMultiAttributeGraphIO(
                            Filters.selfCycleFilter())
                    .read(
                            TaxiSimulation.class.getResourceAsStream(name));

            GRAPH_CACHE.put(name, g);
            return g;
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }



}