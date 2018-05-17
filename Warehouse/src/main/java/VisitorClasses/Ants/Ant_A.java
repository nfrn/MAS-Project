package VisitorClasses.Ants;

import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;

public class Ant_A extends Ant{
    //Now we need to implement what to do to each pheromone along the way
    @Override
    public void dropPheromone(Pheromone_A pheromone) {
        //.out.println("Changed|Added Pheromone A");
    }

    @Override
    public void dropPheromone(Pheromone_B pheromone) {
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
    }
}
