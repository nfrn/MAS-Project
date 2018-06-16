package VisitorClasses.Ants;

import DelgMas.AgvModel;
import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
import com.github.rinde.rinsim.util.TimeWindow;

/**
 * Check Booking ant
 */
public class Ant_Intention extends Ant {
    private TimeWindow timeWindow;
    private int agentID;

    public Ant_Intention(AgvModel agvModel, TimeWindow tw, int agentID) {
        super(agvModel);
        this.timeWindow = tw;
        this.agentID = agentID;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Node_Booking pheromone) {
        long begin = this.timeWindow.begin();
        long end = this.timeWindow.end();
        TimeWindow tw = pheromone.node_booking;
        if (pheromone.getAgentID() != this.agentID)
            if (tw.isIn(begin) || tw.isIn(end) || (tw.isBeforeStart(begin) && tw.isAfterEnd(end))) {
                return -1;
            }
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_Boxes_Info pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Connection_Booking pheromone) {
        long begin = this.timeWindow.begin();
        long end = this.timeWindow.end();
        TimeWindow tw = pheromone.connection_booking;
        if (pheromone.getAgentID() != this.agentID) {
            if (tw.isIn(begin) || tw.isIn(end) || (tw.isBeforeStart(begin) && tw.isAfterEnd(end))) {
                return -1;
            }
        }
        return 0;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public int getAgentID() {
        return agentID;
    }
}
