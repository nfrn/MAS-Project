//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package demo;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.DefaultPDPModel;
import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Graphs;
import com.github.rinde.rinsim.geom.LengthData;
import com.github.rinde.rinsim.geom.ListenableGraph;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.geom.TableGraph;
import com.github.rinde.rinsim.scenario.generator.Depots;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.View.Builder;
import com.github.rinde.rinsim.ui.renderers.AGVRenderer;
import com.github.rinde.rinsim.ui.renderers.GraphRoadModelRenderer;
import com.github.rinde.rinsim.ui.renderers.WarehouseRenderer;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.math3.random.RandomGenerator;
import com.github.rinde.rinsim.ui.renderers.RoadUserRenderer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.measure.unit.SI;
import javax.print.attribute.standard.Destination;

import static com.github.rinde.rinsim.core.model.pdp.PDPModel.ParcelState.PICKING_UP;

public final class AgvExample {
    public static final double VEHICLE_LENGTH = 2.0D;
    public static final int NUM_AGVS = 1;
    public static final int NUM_BOXES = 13;
    public static final long TEST_END_TIME = 600000L;
    public static final int TEST_SPEED_UP = 16;
    public static final long SERVICE_DURATION = 10000;
    public static final int MAX_CAPACITY=1;
    public  static final int NUM_DEPOTS=5;

    private AgvExample() {
    }

    public static void main(String[] args) {
        run(false);
    }

    public static void run(boolean testing) {

        List<Point> depot_locations = new ArrayList<>();
        for(int i = 0; i < NUM_DEPOTS; ++i)
            depot_locations.add(new Point(76,i*12));

        Builder viewBuilder = View.builder().with(
                    WarehouseRenderer.builder().withMargin(2.0D)
                        .withNodes())
                .with(AGVRenderer.builder().withVehicleCoordinates().withDifferentColorsForVehicles())
                .with(RoadUserRenderer.builder()
                        .withImageAssociation(
                                Box.class, "/graphics/perspective/deliverypackage3.png")
                        .withImageAssociation(
                                Depot.class, "/graphics/perspective/tall-building-64.png"))
                        //.withImageAssociation(
                        //        AgvAgent.class, "/graphics/flat/forklift2.png"))
                .withTitleAppendix("AGV example")
                .withResolution(1300,1000);

       final  Simulator sim = Simulator.builder().addModel(RoadModelBuilders.dynamicGraph(AgvExample.GraphCreator.createGraph())
                .withCollisionAvoidance().withDistanceUnit(SI.METER)
                .withVehicleLength(VEHICLE_LENGTH))
                .addModel(DefaultPDPModel.builder())
                .addModel(viewBuilder)
                        .build();

        final RandomGenerator rng = sim.getRandomGenerator();

        final RoadModel roadModel = sim.getModelProvider().getModel(
                RoadModel.class);

        for(int i = 0; i < NUM_BOXES; ++i) {
            sim.register(new Box(Parcel.builder(new Point(0,i*4),
                    roadModel.getRandomPosition(rng))
                    .neededCapacity(MAX_CAPACITY)
                    .pickupTimeWindow(TimeWindow.create(2,4))
                    .deliveryTimeWindow(TimeWindow.create(4,6))
                    .pickupDuration(SERVICE_DURATION)
                    .buildDTO()));
        }

        for(Point loc : depot_locations) {
            sim.register(new Depot(loc));
        }

        for(int i = 0; i < NUM_AGVS; ++i) {
            sim.register(new AgvAgent(roadModel.getRandomPosition(rng),rng));
        }

        sim.addTickListener(new TickListener() {
            @Override
            public void tick(TimeLapse time) {

                PDPModel pdpModel = sim.getModelProvider().getModel(
                        DefaultPDPModel.class);

                for(Parcel parcel : pdpModel.getParcels(PICKING_UP)){
                    if(roadModel.getObjects().size()!=NUM_AGVS+NUM_BOXES+NUM_DEPOTS) {
                        System.out.println("created");
                        sim.register(new Box(Parcel.builder(new Point(parcel.getPickupLocation().x, parcel.getPickupLocation().y),
                                roadModel.getRandomPosition(rng))
                                .neededCapacity(MAX_CAPACITY)
                                .pickupTimeWindow(TimeWindow.create(2, 4))
                                .deliveryTimeWindow(TimeWindow.create(4, 6))
                                .pickupDuration(SERVICE_DURATION)
                                .buildDTO()));

                    }
                }
            }

            @Override
            public void afterTick(TimeLapse timeLapse) {}
        });


        sim.start();
    }

    static class GraphCreator {

        GraphCreator() {
        }

        static ImmutableTable<Integer, Integer, Point> createMatrix(int cols, int rows, Point offset) {
            com.google.common.collect.ImmutableTable.Builder<Integer, Integer, Point> builder = ImmutableTable.builder();

            for(int c = 0; c < cols; ++c) {
                for(int r = 0; r < rows; ++r) {
                    builder.put(r, c, new Point(offset.x + (double)c * 2.0D * 2.0D, offset.y + (double)r * 2.0D * 2.0D));
                }
            }

            return builder.build();
        }

        static ListenableGraph<LengthData> createGraph() {
            Graph<LengthData> g = new TableGraph();
            Table<Integer, Integer, Point> leftMatrix = createMatrix(3, 13, new Point(0.0D, 0.0D));
            Iterator var2 = leftMatrix.columnMap().values().iterator();

            while(var2.hasNext()) {
                Map<Integer, Point> column = (Map)var2.next();
                Graphs.addBiPath(g, column.values());
            }
            Graphs.addBiPath(g, leftMatrix.row(0).values());
            Graphs.addBiPath(g, leftMatrix.row(3).values());
            Graphs.addBiPath(g, leftMatrix.row(6).values());
            Graphs.addBiPath(g, leftMatrix.row(9).values());
            Graphs.addBiPath(g, leftMatrix.row(12).values());

            Table<Integer, Integer, Point> centerMatrix = createMatrix(10, 7, new Point(20.0D, 12.0D));
            Iterator var6 = centerMatrix.rowMap().values().iterator();

            while(var6.hasNext()) {
                Map<Integer, Point> row = (Map)var6.next();
                Graphs.addBiPath(g, row.values());
            }

            Graphs.addBiPath(g, centerMatrix.column(0).values());
            Graphs.addBiPath(g, centerMatrix.column(centerMatrix.columnKeySet().size() - 1).values());
            Graphs.addBiPath(g, new Point[]{(Point)leftMatrix.get(1, 2), (Point)centerMatrix.get(0, 0)});
            Graphs.addBiPath(g, new Point[]{(Point)leftMatrix.get(3, 2), (Point)centerMatrix.get(2, 0)});
            Graphs.addBiPath(g, new Point[]{(Point)leftMatrix.get(6, 2), (Point)centerMatrix.get(3, 0)});
            Graphs.addBiPath(g, new Point[]{(Point)leftMatrix.get(9, 2), (Point)centerMatrix.get(4, 0)});
            Graphs.addBiPath(g, new Point[]{(Point)leftMatrix.get(11, 2), (Point)centerMatrix.get(6, 0)});

            Table<Integer, Integer, Point> rightMatrix = createMatrix(3, 13, new Point(68.0D, 0.0D));
            Iterator var3 = rightMatrix.columnMap().values().iterator();

            while(var3.hasNext()) {
                Map<Integer, Point> column = (Map)var3.next();
                Graphs.addBiPath(g, column.values());
            }
            Graphs.addBiPath(g, rightMatrix.row(0).values());
            Graphs.addBiPath(g, rightMatrix.row(3).values());
            Graphs.addBiPath(g, rightMatrix.row(6).values());
            Graphs.addBiPath(g, rightMatrix.row(9).values());
            Graphs.addBiPath(g, rightMatrix.row(12).values());


            Graphs.addBiPath(g, new Point[]{(Point)rightMatrix.get(1, 0), (Point)centerMatrix.get(0, 9)});
            Graphs.addBiPath(g, new Point[]{(Point)rightMatrix.get(3, 0), (Point)centerMatrix.get(2, 9)});
            Graphs.addBiPath(g, new Point[]{(Point)rightMatrix.get(6, 0), (Point)centerMatrix.get(3, 9)});
            Graphs.addBiPath(g, new Point[]{(Point)rightMatrix.get(9, 0), (Point)centerMatrix.get(4, 9)});
            Graphs.addBiPath(g, new Point[]{(Point)rightMatrix.get(11, 0), (Point)centerMatrix.get(6, 9)});


            return new ListenableGraph(g);
        }
    }
}