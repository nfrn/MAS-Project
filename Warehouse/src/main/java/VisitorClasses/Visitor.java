package VisitorClasses;

import VisitorClasses.Pheromones.PheromoneConnectionBooking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;

public interface Visitor {
    public void dropPheromone(Pheromone_A pheromone);
    public int dropPheromone(Pheromone_B pheromone);
    public void dropPheromone(Pheromone_C pheromone);
    public int dropPheromone(PheromoneConnectionBooking pheromone);
}
