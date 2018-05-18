package VisitorClasses.Ants;

import DelgMas.AgvModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.util.TimeWindow;

public class Ant_C extends Ant {
    private TimeWindow timeWindow;

    public Ant_C(AgvModel agvModel, TimeWindow tw) {
        super(agvModel);
        this.timeWindow = tw;
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
        pheromone.node_booking.add(this.timeWindow);
        //System.out.println("Node: " + pheromone.position + " knows that it is booked:" + pheromone.node_booking);
    }
}
