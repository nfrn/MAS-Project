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

    public Ant_B(AgvModel agvModel, TimeWindow tw) {
        super(agvModel);
        this.timeWindow = tw;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        long begin = this.timeWindow.begin();
        long end = this.timeWindow.end();
        for(TimeWindow tw: pheromone.node_booking){
            if (!(!tw.isAfterStart(end) || tw.isAfterEnd(begin))) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {

    }
}