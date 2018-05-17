package DelgMas;

import VisitorClasses.Ants.Ant;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
import VisitorClasses.Visitable;
import VisitorClasses.Visitor;
import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.Model;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.road.GraphRoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.google.auto.value.AutoValue;

import java.util.*;

public class DMASModel implements TickListener, Model<Point> {

    private RoadModel rm;
    private AgvModel am;
    private GraphRoadModel grm;
    private ArrayList<PheromoneStorage> nodes;

    public DMASModel(RoadModel roadModel, AgvModel agvModel, GraphRoadModel grm) {
        this.rm = roadModel;
        this.am = agvModel;
        this.grm = grm;
        nodes = new ArrayList<PheromoneStorage>();
        for(Point p : grm.getGraph().getNodes()){
            this.nodes.add(new PheromoneStorage(p));
        }
    }

    public void releaseAnts(int type){
        if(type==0){
            Ant_A antA = new Ant_A();
            for(PheromoneStorage pheroStore: nodes){
                pheroStore.accept(antA);
            }
        }
        else if(type==1){
            Ant_B antB = new Ant_B();
            for(PheromoneStorage pheroStore: nodes){
                pheroStore.accept(antB);
            }
        }
        else if(type==2){
            Ant_C antC = new Ant_C();
            for(PheromoneStorage pheroStore: nodes){
                pheroStore.accept(antC);
            }
        }

    }


    static DMASModel.Builder builder() {
        return new AutoValue_DMASModel_Builder();
    }

    @Override
    public void tick(TimeLapse timeLapse) {
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
}