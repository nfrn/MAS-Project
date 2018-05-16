//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package DelgMas;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.*;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.View.Builder;
import com.github.rinde.rinsim.ui.renderers.AGVRenderer;
import com.github.rinde.rinsim.ui.renderers.RoadUserRenderer;
import com.github.rinde.rinsim.ui.renderers.WarehouseRenderer;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.apache.commons.math3.random.RandomGenerator;

import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.rinde.rinsim.core.model.pdp.PDPModel.ParcelState.PICKING_UP;

public final class AgvExample {
    public static final double VEHICLE_LENGTH = 2.0D;
    public static final int NUM_AGVS = 1;
    public static final int NUM_BOXES = 13;
    public static final int NUM_BATTERY = 4;
    public static final int NUM_DEPOTS = 5;
    public static final long TEST_END_TIME = 600000L;
    public static final int TEST_SPEED_UP = 16;
    public static final long SERVICE_DURATION = 10000;
    public static final int MAX_CAPACITY = 1;
    public static List<Point> storage_positions;

    private AgvExample() {
    }

    public static void main(String[] args) {
        run(false);
    }

    public static void run(boolean testing) {

        Builder viewBuilder = View.builder().with(
                WarehouseRenderer.builder().withMargin(2.0D)
                        .withNodes())
                .with(AGVRenderer.builder().withVehicleCoordinates().withDifferentColorsForVehicles())
                .with(AgvRenderer.builder())
                .with(RoadUserRenderer.builder()
                        .withImageAssociation(
                                Box.class, "/graphics/perspective/deliverypackage3.png")
                        .withImageAssociation(
                                Depot.class, "/graphics/perspective/tall-building-64.png")
                        .withImageAssociation(
                                BatteryCharger.class, "/graphics/flat/warehouse-32.png"))
                .withTitleAppendix("AGV example")
                .withResolution(1300, 1000);

        final Simulator sim = Simulator.builder().addModel(RoadModelBuilders.dynamicGraph(AgvExample.GraphCreator.createGraph())
                .withCollisionAvoidance().withDistanceUnit(SI.METER)
                .withVehicleLength(VEHICLE_LENGTH))
                .addModel(AgvModel.builder())
                .addModel(viewBuilder)
                .build();
        final RandomGenerator rng = sim.getRandomGenerator();

        final RoadModel roadModel = sim.getModelProvider().getModel(
                RoadModel.class);
        final AgvModel agvModel = sim.getModelProvider().getModel(
                AgvModel.class);

        // Initial positions
        List<Point> depot_locations = new ArrayList<>();
        for (int i = 0; i < NUM_DEPOTS; ++i)
            depot_locations.add(new Point(76, i * 12));

        storage_positions = new ArrayList<>();
        for (int x = 0; x < 10; ++x) {
            for (int y = 0; y < 7; ++y) {
                storage_positions.add(new Point(20 + x * 4, 12 + y * 4));
            }
        }

        for (int i = 0; i < NUM_BOXES; ++i) {
            sim.register(new Box(Parcel.builder(new Point(0, i * 4),
                    storage_positions.get(rng.nextInt(storage_positions.size())))
                    .neededCapacity(MAX_CAPACITY)
                    .pickupTimeWindow(TimeWindow.create(2, 4))
                    .deliveryTimeWindow(TimeWindow.create(4, 6))
                    .pickupDuration(SERVICE_DURATION)
                    .buildDTO(), false));
        }


        for (Point loc : depot_locations) {
            sim.register(new Depot(loc));
        }

        sim.register(new BatteryCharger(new Point(36.0D, 4.0D)));
        sim.register(new BatteryCharger(new Point(36.0D, 44.0D)));
        sim.register(new BatteryCharger(new Point(40.0D, 4.0D)));
        sim.register(new BatteryCharger(new Point(40.0D, 44.0D)));

        for (int i = 0; i < NUM_AGVS; ++i) {
            sim.register(new AgvAgent(roadModel.getRandomPosition(rng), rng, agvModel));
        }

        sim.addTickListener(new TickListener() {
            @Override
            public void tick(TimeLapse time) {

                AgvModel pdpModel = sim.getModelProvider().getModel(
                        AgvModel.class);

                for (Parcel parcel : pdpModel.getParcels(PICKING_UP)) {
                    if (roadModel.getObjects().size() != NUM_AGVS + NUM_BOXES + NUM_DEPOTS + NUM_BATTERY) {
                        sim.register(new Box(Parcel.builder(new Point(parcel.getPickupLocation().x, parcel.getPickupLocation().y),
                                storage_positions.get(rng.nextInt(storage_positions.size())))
                                .neededCapacity(MAX_CAPACITY)
                                .pickupTimeWindow(TimeWindow.create(2, 4))
                                .deliveryTimeWindow(TimeWindow.create(4, 6))
                                .pickupDuration(SERVICE_DURATION)
                                .buildDTO(), false));

                    }
                }
            }

            @Override
            public void afterTick(TimeLapse timeLapse) {
            }
        });


        sim.start();
    }

    static class GraphCreator {

        GraphCreator() {
        }

        static ImmutableTable<Integer, Integer, Point> createMatrix(int cols, int rows, Point offset) {
            ImmutableTable.Builder<Integer, Integer, Point> builder = ImmutableTable.builder();

            for (int c = 0; c < cols; ++c) {
                for (int r = 0; r < rows; ++r) {
                    builder.put(r, c, new Point(offset.x + (double) c * 2.0D * 2.0D, offset.y + (double) r * 2.0D * 2.0D));
                }
            }

            return builder.build();
        }

        static ListenableGraph<LengthData> createGraph() {
            Graph<LengthData> g = new TableGraph();
            Table<Integer, Integer, Point> leftMatrix = createMatrix(3, 13, new Point(0.0D, 0.0D));
            Iterator var2 = leftMatrix.columnMap().values().iterator();

            while (var2.hasNext()) {
                Map<Integer, Point> column = (Map) var2.next();
                Graphs.addBiPath(g, column.values());
            }
            Graphs.addBiPath(g, leftMatrix.row(0).values());
            Graphs.addBiPath(g, leftMatrix.row(3).values());
            Graphs.addBiPath(g, leftMatrix.row(6).values());
            Graphs.addBiPath(g, leftMatrix.row(9).values());
            Graphs.addBiPath(g, leftMatrix.row(12).values());

            Table<Integer, Integer, Point> centerMatrix = createMatrix(10, 7, new Point(20.0D, 12.0D));
            Iterator var6 = centerMatrix.rowMap().values().iterator();

            while (var6.hasNext()) {
                Map<Integer, Point> row = (Map) var6.next();
                Graphs.addBiPath(g, row.values());
            }

            Graphs.addBiPath(g, centerMatrix.column(0).values());
            Graphs.addBiPath(g, centerMatrix.column(centerMatrix.columnKeySet().size() - 1).values());
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(1, 2), (Point) centerMatrix.get(0, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(3, 2), (Point) centerMatrix.get(2, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(6, 2), (Point) centerMatrix.get(3, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(9, 2), (Point) centerMatrix.get(4, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(11, 2), (Point) centerMatrix.get(6, 0)});

            Table<Integer, Integer, Point> rightMatrix = createMatrix(3, 13, new Point(68.0D, 0.0D));
            Iterator var3 = rightMatrix.columnMap().values().iterator();

            while (var3.hasNext()) {
                Map<Integer, Point> column = (Map) var3.next();
                Graphs.addBiPath(g, column.values());
            }
            Graphs.addBiPath(g, rightMatrix.row(0).values());
            Graphs.addBiPath(g, rightMatrix.row(3).values());
            Graphs.addBiPath(g, rightMatrix.row(6).values());
            Graphs.addBiPath(g, rightMatrix.row(9).values());
            Graphs.addBiPath(g, rightMatrix.row(12).values());


            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(1, 0), (Point) centerMatrix.get(0, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(3, 0), (Point) centerMatrix.get(2, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(6, 0), (Point) centerMatrix.get(3, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(9, 0), (Point) centerMatrix.get(4, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(11, 0), (Point) centerMatrix.get(6, 9)});


            //Battery Charge1

            Graphs.addBiPath(g, new Point(36.0D, 4.0D), (Point) centerMatrix.get(0, 3));
            Graphs.addBiPath(g, new Point(36.0D, 4.0D), new Point(40.0D, 4.0D));
            Graphs.addBiPath(g, new Point(40.0D, 4.0D), (Point) centerMatrix.get(0, 6));


            //Battery Charge2

            Graphs.addBiPath(g, new Point(36.0D, 44.0D), (Point) centerMatrix.get(6, 3));
            Graphs.addBiPath(g, new Point(36.0D, 44.0D), new Point(40.0D, 44.0D));
            Graphs.addBiPath(g, new Point(40.0D, 44.0D), (Point) centerMatrix.get(6, 6));

            return new ListenableGraph(g);
        }
    }
}