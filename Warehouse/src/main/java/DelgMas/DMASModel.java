package DelgMas;

import Ui.RoadDataPanel;
import Ui.StringListener;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_Intention;
import VisitorClasses.Ants.Ant_Booking;
import VisitorClasses.Ants.Ant_Path_Finder;
import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Ants.*;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.Model;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.road.GraphRoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.auto.value.AutoValue;

import javax.swing.*;
import java.util.*;

import static DelgMas.AgvExample.TICK_LENGTH;

public class DMASModel implements TickListener, Model<Point> {

    private static final long ANT_A_FREQUENCY = 40 * TICK_LENGTH;
    private int clock_A;
    public RoadModel rm;
    public AgvModel am;
    public GraphRoadModel graphRoadModel;
    public final Map<Point, PheromoneStorage> nodes;
    public final Map<Connection, PheromoneConnectionStorage> connections;
    private StringListener stringListener;


    public DMASModel(RoadModel roadModel, AgvModel agvModel, GraphRoadModel grm) {
        this.rm = roadModel;
        this.am = agvModel;
        this.graphRoadModel = grm;
        this.clock_A = 0;
        nodes = new HashMap<Point, PheromoneStorage>();
        for (Point p : graphRoadModel.getGraph().getNodes()) {
            this.nodes.put(p, new PheromoneStorage(p, new ArrayList<Point>(grm.getGraph().getOutgoingConnections(p))));
        }
        connections = new HashMap<Connection, PheromoneConnectionStorage>();
        for (Connection c : graphRoadModel.getGraph().getConnections()) {
            this.connections.put(c, new PheromoneConnectionStorage(c));
        }

        this.releaseAnts_Boxs_Info();
        //createUi(this);
    }

    public void releaseAnts_A() {
        //Go to chargers and see their availabiliy. Register that in Pheromone_A
        Ant_A antA = new Ant_A(am);
        for (PheromoneStorage pheroStore : nodes.values()) {
            pheroStore.accept(antA);
        }
    }

    public void release_booking(Queue<Point> path, AgvAgent agent) {
        List<Point> pathList = new ArrayList<>(path);

        int i = 0;
        for (int j = 0; j < pathList.size(); j++) {
            Point pt = pathList.get(j);

            PheromoneStorage pheroStore = nodes.get(pt);
            // check if the node is booked
            if (pheroStore != null) {

                for(Iterator<Pheromone_Node_Booking> iter = pheroStore.list_phero_B.iterator(); iter.hasNext(); ) {
                    Pheromone_Node_Booking phero = iter.next();
                    if(phero.getAgentID() == agent.ID)
                        iter.remove();
                }
            }

            if(j+1 < pathList.size()) {
                Point to = pathList.get(j + 1);

                Point from = pt;
                if(!this.graphRoadModel.getGraph().containsNode(pt))
                    from = this.graphRoadModel.getConnection(agent).get().from();

                Connection c = this.graphRoadModel.getGraph().getConnection(from, to);
                PheromoneConnectionStorage pcs = connections.get(c);

                for(Iterator<Pheromone_Connection_Booking> iter = pcs.list_phero_Connection_Booking.iterator(); iter.hasNext(); ) {
                    Pheromone_Connection_Booking phero = iter.next();
                    if(phero.getAgentID() == agent.ID)
                        iter.remove();
                }
            }
        }
    }

    public int releaseAnts_CheckBooking(Queue<Point> path, List<TimeWindow> tws, int agentID, AgvAgent agent) {
        //Go to Path and check if it is free
        List<Point> pathList = new ArrayList<>(path);

        int i = 0;
        for (int j = 0; j < pathList.size(); j++) {
            Point pt = pathList.get(j);

            PheromoneStorage pheroStore = nodes.get(pt);
            // check if the node is booked
            if (pheroStore != null) {
                Ant_Intention antB = new Ant_Intention(am, tws.get(i), agentID);
                int result = pheroStore.accept(antB);
                if (result == -1) {
                    return j;
                }
                antB = null;
            }

            // check if the connection to the next node is booked
            if(j+1 < pathList.size()) {
                Point to = pathList.get(j + 1);
                TimeWindow next_tw = tws.get(i+1);
                long end = next_tw.begin() > tws.get(i).end() ? next_tw.begin() : tws.get(i).end() + 2 * AgvAgent.VISIT_TIME_LENGTH;
                TimeWindow tw = TimeWindow.create(Math.max(0, tws.get(i).end() - 2 * AgvAgent.VISIT_TIME_LENGTH), end + 2 * AgvAgent.VISIT_TIME_LENGTH);


                Point from = pt;
                if(!this.graphRoadModel.getGraph().containsNode(pt))
                    from = this.graphRoadModel.getConnection(agent).get().from();

                Connection c = this.graphRoadModel.getGraph().getConnection(to, from);
                PheromoneConnectionStorage pcs = connections.get(c);
                Ant_Intention antB = new Ant_Intention(am, tw, agentID);
                if (pcs.accept(antB) == -1)
                    return j;


                antB = null;
            }
            i++;
        }
        return -1;
    }

    public void releaseAnts_BookingOnConnection(Connection c, TimeWindow tw, AgvAgent agent) {
        PheromoneConnectionStorage pcs = connections.get(c);
        Ant_Booking antC = new Ant_Booking(am, tw, agent.ID);
        pcs.accept(antC);
    }

    public void releaseAnts_Booking(Queue<Point> path, List<TimeWindow> tws, int agentID, AgvAgent agent) {
        //Go to Path and tell to book it

        List<Point> pathList = new ArrayList<>(path);

        int i = 0;
        for (int j = 0; j < pathList.size(); j++) {
            Point pt = pathList.get(j);
            PheromoneStorage pheroStore = nodes.get(pt);


            if(j+1 < pathList.size()) {
                Point to = pathList.get(j+1);
                TimeWindow next_tw = tws.get(i+1);
                long end = next_tw.begin() > tws.get(i).end() ? next_tw.begin() : tws.get(i).end() + 2 * AgvAgent.VISIT_TIME_LENGTH;
                TimeWindow tw = TimeWindow.create(Math.max(0, tws.get(i).end() - 2 * AgvAgent.VISIT_TIME_LENGTH), end + 2 * AgvAgent.VISIT_TIME_LENGTH);

                Point from = pt;
                if(!this.graphRoadModel.getGraph().containsNode(pt))
                    from = this.graphRoadModel.getConnection(agent).get().from();

                if(!this.graphRoadModel.getGraph().containsNode(to))
                    to = this.graphRoadModel.getConnection(agent).get().to();

                Connection c = this.graphRoadModel.getGraph().getConnection(from, to);
                PheromoneConnectionStorage pcs = connections.get(c);
                Ant_Booking antC = new Ant_Booking(am, tw, agentID);
                pcs.accept(antC);

                antC = null;
            }

            if (pheroStore != null) {
                Ant_Booking antC = new Ant_Booking(am, tws.get(i), agentID);
                pheroStore.accept(antC);

                antC = null;
            }
            i++;
        }
    }

    public Queue<Point> releaseAnts_D(Point orig, Point desti, int delay) {

        Ant_Path_Finder antD = new Ant_Path_Finder(am, this, orig, desti);
        return antD.getPath(delay);

    }

    public void releaseAnts_Boxs_Info() {

        Ant_Boxs_Info ant_boxs_info = new Ant_Boxs_Info(am);
        for (PheromoneStorage pheroStore : nodes.values()) {
            pheroStore.accept(ant_boxs_info);
        }
    }

    public PheromoneConnectionStorage getConnection(Point from, Point to) {
        try {
            Connection c = this.graphRoadModel.getGraph().getConnection(from, to);
            return this.connections.get(c);
        }catch (Exception e) {
            return null;
        }
    }

    //**
    // DEPRECATED
    //**
    public Point getClosestNode(Point currPoint) {
        Set<Point> nodes = this.graphRoadModel.getGraph().getNodes();
        double minDist = Double.MAX_VALUE;
        Point closestPoint = null;

        for (Point p : nodes) {
            double dist = Math.sqrt(Math.pow(currPoint.x - p.x, 2) + Math.pow(currPoint.y - p.y, 2));
            if (dist < minDist) {
                minDist = dist;
                closestPoint = p;
            }
        }
        return closestPoint;
    }



    static DMASModel.Builder builder() {
        return new AutoValue_DMASModel_Builder();
    }

    @Override
    public void tick(TimeLapse timeLapse) {

        this.clock_A += timeLapse.getTime();

        if (this.clock_A >= ANT_A_FREQUENCY) {
            this.clock_A = 0;
            this.releaseAnts_A();
            this.releaseAnts_Boxs_Info();
            //this.stringListener.inputEmitted(new ArrayList<PheromoneStorage>(nodes.values()));
            //am.taskListener.inputEmitted(am.getBoxes());
        }

        for (PheromoneStorage phestore : nodes.values()) {
            phestore.time_passed();
        }
        for (PheromoneConnectionStorage phestore : connections.values()) {
            phestore.time_passed();
        }

    }


    @AutoValue
    abstract static class Builder
            extends ModelBuilder.AbstractModelBuilder<DMASModel, Point> {
        private static final long serialVersionUID = 3349625514419113101L;

        Builder() {
            setDependencies(RoadModel.class, AgvModel.class, GraphRoadModel.class);
        }

        @Override
        public DMASModel build(DependencyProvider dependencyProvider) {
            final RoadModel rm = dependencyProvider.get(RoadModel.class);
            final AgvModel pm = dependencyProvider.get(AgvModel.class);
            final GraphRoadModel grm = dependencyProvider.get(GraphRoadModel.class);
            return new DMASModel(rm, pm, grm);
        }
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
    }

    @Override
    public boolean register(Point pointPheromoneStorageMap) {
        return false;
    }

    @Override
    public boolean unregister(Point pointPheromoneStorageMap) {
        return false;
    }

    @Override
    public Class<Point> getSupportedType() {
        return Point.class;
    }

    @Override
    public <U> U get(Class<U> aClass) {
        return null;
    }

    public void createUi(final DMASModel dmasModel) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RoadDataPanel panel = new RoadDataPanel(dmasModel, new ArrayList<PheromoneStorage>(nodes.values()));
                panel.setVisible(true);
            }
        });
    }

    public void setStringListener(StringListener stringListener) {
        this.stringListener = stringListener;
    }
}