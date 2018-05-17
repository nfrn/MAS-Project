package VisitorClasses.Ants;

import DelgMas.AgvModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import VisitorClasses.Visitor;

public class Ant implements Visitor {
    public AgvModel agv;
    public Ant(AgvModel agvModel){
        agv=agvModel;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public void dropPheromone(Pheromone_B pheromone) {

    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {

    }
}
