package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.PheromoneStorage;
import VisitorClasses.Pheromones.PheromoneConnectionBooking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.util.TimeWindow;

public class Ant_C extends Ant {
    private TimeWindow timeWindow;
    private int agentID;

    public Ant_C(AgvModel agvModel, TimeWindow tw, int agentID) {
        super(agvModel);
        this.timeWindow = tw;
        this.agentID = agentID;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        pheromone.lifetime = PheromoneStorage.LIFETIME_B;
        pheromone.node_booking = this.getTimeWindow();
        return 0;
    }

    @Override
    public int dropPheromone(PheromoneConnectionBooking pheromone) {
        pheromone.lifetime = PheromoneStorage.LIFETIME_B;
        pheromone.connection_booking = this.getTimeWindow();

        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public int getAgentID() {
        return agentID;
    }
}
