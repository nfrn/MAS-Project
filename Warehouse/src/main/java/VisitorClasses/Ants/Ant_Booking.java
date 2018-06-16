package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.PheromoneStorage;
import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
import com.github.rinde.rinsim.util.TimeWindow;

/**
 * Booking ant
 */
public class Ant_Booking extends Ant {
    private TimeWindow timeWindow;
    private int agentID;

    public Ant_Booking(AgvModel agvModel, TimeWindow tw, int agentID) {
        super(agvModel);
        this.timeWindow = tw;
        this.agentID = agentID;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Node_Booking pheromone) {
        pheromone.lifetime = PheromoneStorage.LIFETIME_B;
        pheromone.node_booking = this.getTimeWindow();
        return 0;
    }

    @Override
    public int dropPheromone(Pheromone_Connection_Booking pheromone) {
        pheromone.lifetime = PheromoneStorage.LIFETIME_B;
        pheromone.connection_booking = this.getTimeWindow();

        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_Boxes_Info pheromone) {
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public int getAgentID() {
        return agentID;
    }
}
