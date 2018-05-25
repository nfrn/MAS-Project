package DelgMas;

import Ui.RoadDataPanel;
import Ui.StringListener;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
import VisitorClasses.Ants.Ant_D;
import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.Model;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.road.GraphRoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;
import com.google.auto.value.AutoValue;

import javax.swing.*;
import java.text.Collator;
import java.util.*;

import static DelgMas.AgvExample.TICK_LENGTH;

public class DMASModel implements TickListener, Model<Point> {

    private static final long ANT_A_FREQUENCY = 40* TICK_LENGTH;
    private static final long PATHS = 5;
    private int clock_A;
    public RoadModel rm;
    public AgvModel am;
    public GraphRoadModel grm;
    public final Map<Point,PheromoneStorage> nodes;
    private StringListener stringListener;


    public DMASModel(RoadModel roadModel, AgvModel agvModel, GraphRoadModel grm) {
        this.rm = roadModel;
        this.am = agvModel;
        this.grm = grm;
        this.clock_A = 0;
        nodes = new HashMap<Point,PheromoneStorage>();
        for(Point p : grm.getGraph().getNodes()){
            this.nodes.put(p,new PheromoneStorage(p,new ArrayList<Point>(grm.getGraph().getOutgoingConnections(p))));
        }
        createUi(this);
    }

    public void releaseAnts_A(){
        //Go to chargers and see their availabiliy. Register that in Pheromone_A
        //System.out.println("Ants_A released");
        Ant_A antA = new Ant_A(am);
        for(PheromoneStorage pheroStore: nodes.values()){
            pheroStore.accept(antA);
        }
    }

    public int releaseAnts_B(Queue<Point> path, List<TimeWindow> tws, int agentID) {
        //Go to Path and check if it is free
        //System.out.println("Ants_B released");
        int i = 0;
        for (Point pt : path) {
            PheromoneStorage pheroStore = nodes.get(pt);
            if (pheroStore != null) {
                Ant_B antB = new Ant_B(am, tws.get(i), agentID);
                int result = pheroStore.accept(antB);
                if (result == -1) {
                    return -1;
                }
                i++;
                antB = null;
            }
        }
        return 0;
    }

    public void releaseAnts_C(Queue<Point> path, List<TimeWindow> tws, int agentID) {
        //Go to Path and tell to book it
        //System.out.println("Ants_C released");

        int i = 0;
        for (Point pt : path) {
            PheromoneStorage pheroStore = nodes.get(pt);
            if (pheroStore != null) {
                Ant_C antC = new Ant_C(am, tws.get(i), agentID);
                pheroStore.accept(antC);
                i++;
                antC = null;
            }
        }
    }

    public Queue<Queue<Point>> releaseAnts_D(Point orig, Point desti){

        Queue<Queue<Point>> list = new LinkedList<Queue<Point>>();
        for(int x = 0; x <PATHS; x++){
            Ant_D antD = new Ant_D(am, this, orig, desti);
            list.add(antD.getPath());
        }
        Collections.sort((List<Queue<Point>>) list, new Comparator<Queue<Point>>() {
            @Override
            public int compare(Queue<Point> o1, Queue<Point> o2) {
                if(o1.size() > o2.size()){
                    return 1;
                }
                if(o1.size() < o2.size()){
                    return -1;
                }
                else{
                    return 0;
                }
            }
        });
        return list;
    }


    static DMASModel.Builder builder() {
        return new AutoValue_DMASModel_Builder();
    }

    @Override
    public void tick(TimeLapse timeLapse) {
        this.clock_A += timeLapse.getTickLength();

        if (this.clock_A >= ANT_A_FREQUENCY) {
            this.clock_A = 0;
            this.releaseAnts_A();
            this.stringListener.inputEmitted(new ArrayList<PheromoneStorage>(nodes.values()));
            am.taskListener.inputEmitted(am.getBoxes());
        }

        for(PheromoneStorage phestore : nodes.values()){
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