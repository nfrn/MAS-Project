package VisitorClasses.Ants;

import DelgMas.AgvModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.Queue;

public class Ant_B extends Ant {
    private TimeWindow timeWindow;
    private int agentID;

    public Ant_B(AgvModel agvModel, TimeWindow tw, int agentID) {
        super(agvModel);
        this.timeWindow = tw;
        this.agentID = agentID;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        long begin = this.timeWindow.begin();
        long end = this.timeWindow.end();
        TimeWindow tw = pheromone.node_booking;
        //if (!(!tw.isAfterStart(end) || tw.isBeforeEnd(begin))) {
        if (pheromone.getAgentID() != this.agentID)
            if (tw.isIn(begin) || tw.isIn(end)) {
                return -1;
            }
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
