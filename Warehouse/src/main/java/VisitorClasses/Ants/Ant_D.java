package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.DMASModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.geom.GeomHeuristics;
import com.github.rinde.rinsim.geom.Point;

import javax.measure.Measure;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static DelgMas.AgvAgent.SPEED;

public class Ant_D extends Ant {

    DMASModel dmas;
    Point desti;
    Point origi;

    public Ant_D(AgvModel agvModel, DMASModel dmasModel, Point origi, Point desti) {
        super(agvModel);
        this.dmas = dmasModel;
        this.origi = origi;
        this.desti = desti;
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

    }

    public Queue<Point> getPath() {
        Queue<Point> solution;
        Point nextPoint = origi;
        if(origi.equals(desti)){
            solution = new LinkedList<>();
            return solution;
        }
        double dist = Math.sqrt(Math.pow(origi.x - desti.x, 2) + Math.pow(origi.y - desti.y, 2));
        do{
            solution = new LinkedList();
            nextPoint = origi;
            while (!nextPoint.equals(desti)) {
                ArrayList<Point> roads = dmas.nodes.get(nextPoint).neighbors;
                nextPoint = roads.get((int) (Math.random() * roads.size()));
                solution.add(nextPoint);
            }
        }while(agv.getRoadModel().getDistanceOfPath(solution).getValue() >= (dist*2));
        return solution;
    }
}
