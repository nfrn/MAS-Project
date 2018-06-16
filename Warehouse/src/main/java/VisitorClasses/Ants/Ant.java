package VisitorClasses.Ants;

import DelgMas.AgvModel;
import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
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
    public int dropPheromone(Pheromone_Node_Booking pheromone) {
        return 1;
    }

    @Override
    public void dropPheromone(Pheromone_Boxes_Info pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Connection_Booking pheromone) {
        return 0;
    }
}
