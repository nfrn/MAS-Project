package VisitorClasses;

import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;

public interface Visitor {
    public void dropPheromone(Pheromone_A pheromone);
    public int dropPheromone(Pheromone_Node_Booking pheromone);
    public void dropPheromone(Pheromone_Boxes_Info pheromone);
    public int dropPheromone(Pheromone_Connection_Booking pheromone);
}
