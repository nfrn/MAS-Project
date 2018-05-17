package VisitorClasses.Ants;

import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;

public class Ant_C extends Ant {
    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public void dropPheromone(Pheromone_B pheromone) {

    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
        //System.out.println("Changed|Added Pheromone C");
    }
}
