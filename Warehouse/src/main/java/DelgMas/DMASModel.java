package DelgMas;

import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.Model;
import com.github.rinde.rinsim.core.model.ModelBuilder;
import com.github.rinde.rinsim.core.model.road.GraphRoadModel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DMASModel implements TickListener, Model<Point> {

    private RoadModel rm;
    private AgvModel am;
    private GraphRoadModel grm;
    private HashMap<Point, PheromoneStorage> nodes;

    public DMASModel(RoadModel roadModel, AgvModel agvModel, GraphRoadModel grm) {
        this.rm = roadModel;
        this.am = agvModel;
        this.grm = grm;
        this.nodes = new HashMap<Point, PheromoneStorage>();

        for(Point p : grm.getGraph().getNodes()){
            this.nodes.put(p,new PheromoneStorage(100));
        }

    }

    static DMASModel.Builder builder() {
        return new AutoValue_DMASModel_Builder();
    }

    @Override
    public void tick(TimeLapse timeLapse) {

        Iterator<HashMap.Entry<Point, PheromoneStorage>> itr = this.nodes.entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<Point, PheromoneStorage> entry = itr.next();
            PheromoneStorage p = entry.getValue();
            int result = p.timepassing(1);
            if( result == -1){
                entry.setValue(new PheromoneStorage(100));
            }
            else{
                entry.setValue(p);
            }
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
}