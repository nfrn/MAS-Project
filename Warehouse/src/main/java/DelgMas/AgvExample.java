//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package DelgMas;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModelBuilders;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.core.model.time.TimeModel;
import com.github.rinde.rinsim.geom.*;
import com.github.rinde.rinsim.pdptw.common.StatsPanel;
import com.github.rinde.rinsim.pdptw.common.StatsTracker;
import com.github.rinde.rinsim.scenario.Scenario;
import com.github.rinde.rinsim.scenario.ScenarioController;
import com.github.rinde.rinsim.ui.View;
import com.github.rinde.rinsim.ui.View.Builder;
import com.github.rinde.rinsim.ui.renderers.AGVRenderer;
import com.github.rinde.rinsim.ui.renderers.RoadUserRenderer;
import com.github.rinde.rinsim.ui.renderers.WarehouseRenderer;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.apache.commons.math3.random.RandomGenerator;

import javax.measure.quantity.Duration;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.rinde.rinsim.core.model.pdp.PDPModel.ParcelState.AVAILABLE;
import static com.github.rinde.rinsim.core.model.pdp.PDPModel.ParcelState.PICKING_UP;

public final class AgvExample {
    //Elements
    public static final double VEHICLE_LENGTH = 1.5D;
    public static final int NUM_AGVS = 10;
    public static final int NUM_BOXES = 13;
    public static final int NUM_BATTERY = 4;
    public static final int NUM_DEPOTS = 5;
    public static final int MAX_CAPACITY = 1;
    public static final double PROB_BOX = 0.01;
    //Positions
    public static List<Point> box_positions;
    public static List<Point> storage_positions;
    public static List<Point> charger_positions;
    public static List<Point> depot_positions;
    public static List<Point> agv_positions;
    //Time
    public static final long TICK_LENGTH= 1000;

    private AgvExample() {
    }

    public static void main(String[] args) {

        run(false);
    }

    public static void run(boolean testing) {

        Builder viewBuilder = View.builder().with(
                WarehouseRenderer.builder().withNodes().withMargin(2.0D))
                .with(AGVRenderer.builder().withVehicleCoordinates().withDifferentColorsForVehicles())
                .with(AgvRenderer.builder())
                .with(BoxRender.builder())
                .with(RoadUserRenderer.builder()
                        .withImageAssociation(
                                Box.class, "/graphics/perspective/deliverypackage3.png")
                        .withImageAssociation(
                                Depot.class, "/graphics/perspective/tall-building-64.png")
                        .withImageAssociation(
                                BatteryCharger.class, "/graphics/flat/warehouse-32.png"))
                .withTitleAppendix("AGV example")
                //.with(StatsPanel.builder())
                .withResolution(1300, 1000);

        final Simulator sim = Simulator.builder()
                .setTickLength(TICK_LENGTH)
                .addModel(RoadModelBuilders.dynamicGraph(GraphCreator.createGraph())
                .withCollisionAvoidance().withDistanceUnit(SI.METER)
                .withVehicleLength(VEHICLE_LENGTH))
                .addModel(AgvModel.builder())
                .addModel(DMASModel.builder())
                .addModel(ScenarioController.builder(Scenario.builder()
                        .build()))
                .addModel(StatsTracker.builder())
                .addModel(viewBuilder)
                .build();
        final RandomGenerator rng = sim.getRandomGenerator();

        final DMASModel dmasModel = sim.getModelProvider().getModel(DMASModel.class);
        final RoadModel roadModel = sim.getModelProvider().getModel(
                RoadModel.class);
        final AgvModel agvModel = sim.getModelProvider().getModel(
                AgvModel.class);

        // Initial positions
        depot_positions = new ArrayList<>();
        for (int i = 0; i < NUM_DEPOTS; ++i)
            depot_positions.add(new Point(76, i * 12));

        storage_positions = new ArrayList<>();
        for (int x = 0; x < 10; ++x) {
            for (int y = 0; y < 7; ++y) {
                storage_positions.add(new Point(20 + x * 4, 12 + y * 4));
            }
        }

        agv_positions = new ArrayList<>();
        for (int x = 0; x < 7; x++) {
            agv_positions.add(new Point(8, (x * 4)*2));
        }
        for (int x = 0; x < 7; x++) {
            agv_positions.add(new Point(68, (x * 4)*2));
        }

        box_positions = new ArrayList<>();
        for (int x = 0; x < NUM_BOXES; ++x) {
            box_positions.add(new Point(0, x * 4));
        }
        charger_positions = new ArrayList<>();
        charger_positions.add(new Point(36.0D,4.0D));
        charger_positions.add(new Point(36.0D,44.0D));
        charger_positions.add(new Point(40.0D,4.0D));
        charger_positions.add(new Point(40.0D,44.0D));

        //Register
        for (int i = 0; i < NUM_BOXES; ++i) {
            sim.register(new Box(box_positions.get(i),
                    storage_positions.get(rng.nextInt(storage_positions.size())), 0,false));
            }

        for (Point loc : depot_positions) {
            sim.register(new Depot(loc));
        }

        for (Point loc : charger_positions) {
            sim.register(new BatteryCharger(loc));
        }

        for (int i = 0; i < NUM_AGVS; ++i) {
            Point position = agv_positions.get(i);
            sim.register(new AgvAgent(position, rng, agvModel,dmasModel, i));
            sim.register(new Battery(position));
        }

        sim.addTickListener(new TickListener() {
            @Override
            public void tick(TimeLapse time) {


                TimeModel tm = sim.getModelProvider().getModel(TimeModel.class);
                long currentTime = tm.getCurrentTime();
                //System.out.println(currentTime);

                for (Parcel parcel : agvModel.getParcels(PDPModel.ParcelState.IN_CARGO)) {
                    if(Math.random()<PROB_BOX) {
                        boolean is_beg = this.was_from_begining(parcel.getPickupLocation());
                        boolean is_anything_there = this.is_anything_there(parcel.getPickupLocation());

                        if (is_beg && !is_anything_there) {
                            //System.out.println("Added in fountain");
                            sim.register(new Box(new Point(parcel.getPickupLocation().x, parcel.getPickupLocation().y),
                                    storage_positions.get(rng.nextInt(storage_positions.size())), currentTime, false));
                        }
                    }

                }

            }

            public boolean is_anything_there(Point orig){
                for(RoadUser ruser: roadModel.getObjects()){
                    if(roadModel.getPosition(ruser).equals(orig)){
                        return true;
                    }
                }
                return false;
            }

            public boolean was_from_begining(Point orig){
                boolean is_from_there = false;
                for(Point position : box_positions){
                    if(orig.equals(position)){
                        is_from_there= true;
                    }
                }
                return is_from_there;
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
            Graphs.addBiPath(g, centerMatrix.column(2).values());
            Graphs.addBiPath(g, centerMatrix.column(4).values());
            Graphs.addBiPath(g, centerMatrix.column(5).values());
            Graphs.addBiPath(g, centerMatrix.column(7).values());
            Graphs.addBiPath(g, centerMatrix.column(centerMatrix.columnKeySet().size() - 1).values());
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(1, 2), (Point) centerMatrix.get(0, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(3, 2), (Point) centerMatrix.get(1, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(4, 2), (Point) centerMatrix.get(2, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(6, 2), (Point) centerMatrix.get(3, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(8, 2), (Point) centerMatrix.get(4, 0)});
            Graphs.addBiPath(g, new Point[]{(Point) leftMatrix.get(9, 2), (Point) centerMatrix.get(5, 0)});
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
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(3, 0), (Point) centerMatrix.get(1, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(4, 0), (Point) centerMatrix.get(2, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(6, 0), (Point) centerMatrix.get(3, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(8, 0), (Point) centerMatrix.get(4, 9)});
            Graphs.addBiPath(g, new Point[]{(Point) rightMatrix.get(9, 0), (Point) centerMatrix.get(5, 9)});
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