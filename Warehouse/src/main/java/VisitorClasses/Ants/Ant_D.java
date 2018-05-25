package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.DMASModel;
import DelgMas.PheromoneStorage;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;
import java.util.Collection;

public class Ant_D extends Ant {

    DMASModel dmas;

    public Ant_D(AgvModel agvModel, DMASModel dmasModel) {
        super(agvModel);
        this.dmas=dmasModel;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
        Point position = pheromone.position;
        Collection<Point> outgoingConnections = dmas.grm.getGraph().getOutgoingConnections(position);
        pheromone.neighbors=new ArrayList<Point>(outgoingConnections);
    }
}
