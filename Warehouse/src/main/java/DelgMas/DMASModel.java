package DelgMas;

import Ui.RoadDataPanel;
import Ui.StringListener;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
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
import java.util.*;

import static DelgMas.AgvExample.TICK_LENGTH;

public class DMASModel implements TickListener, Model<Point>{

    private static final long ANT_A_FREQUENCY = 40* TICK_LENGTH;
    private int clock_A;
    private RoadModel rm;
    private AgvModel am;
    public GraphRoadModel grm;
    private final ArrayList<PheromoneStorage> nodes;
    private StringListener stringListener;


    public DMASModel(RoadModel roadModel, AgvModel agvModel, GraphRoadModel grm) {
        this.rm = roadModel;
        this.am = agvModel;
        this.grm = grm;
        this.clock_A = 0;
        nodes = new ArrayList<PheromoneStorage>();
        for(Point p : grm.getGraph().getNodes()){
            this.nodes.add(new PheromoneStorage(p));
        }
        createUi(this);
    }

    public void releaseAnts_A(){
        //Go to chargers and see their availabiliy. Register that in Pheromone_A
        //System.out.println("Ants_A released");
        Ant_A antA = new Ant_A(am);
        for(PheromoneStorage pheroStore: nodes){
            pheroStore.accept(antA);
        }
    }

    public int releaseAnts_B(Queue<Point> path, TimeWindow tw){
        //Go to Path and check if it is free
        //System.out.println("Ants_B released");
        Ant_B antB = new Ant_B(am,tw);
        for(Point pt : path) {
            for (PheromoneStorage pheroStore : nodes) {
                if (pheroStore.position.equals(pt)){
                    int result = pheroStore.accept(antB);
                    if(result==-1){
                        return -1;
                    }
                }
            }
        }
        antB=null;
        return 0;
    }

    public void releaseAnts_C(Queue<Point> path, List<TimeWindow> tws){
        //Go to Path and tell to book it
        //System.out.println("Ants_C released");

        int i = 0;
        for(Point pt : path) {
            for (PheromoneStorage pheroStore : nodes) {
                if (pheroStore.position.equals(pt)){
                    Ant_C antC = new Ant_C(am, tws.get(i));
                    pheroStore.accept(antC);
                    i++;
                    antC=null;
                }
            }
        }
    }


    static DMASModel.Builder builder() {
        return new AutoValue_DMASModel_Builder();
    }

    @Override
    public void tick(TimeLapse timeLapse) {
        this.clock_A += timeLapse.getTickLength();

        if(this.clock_A >= ANT_A_FREQUENCY) {
            this.clock_A=0;
            this.releaseAnts_A();
            this.stringListener.inputEmitted(nodes);
            am.taskListener.inputEmitted(am.getBoxes());
        }

        for(PheromoneStorage phestore : nodes){
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

    public void createUi(final DMASModel dmasModel){
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RoadDataPanel panel = new RoadDataPanel(dmasModel, nodes);
                panel.setVisible(true);
            }
        });
    }

    public void setStringListener(StringListener stringListener){
        this.stringListener=stringListener;
    }
}