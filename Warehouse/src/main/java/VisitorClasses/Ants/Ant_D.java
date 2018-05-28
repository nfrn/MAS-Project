package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.DMASModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
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
        Queue<Point> solution = new LinkedList();
        Point current = origi;
        Point nextPoint;
        //System.out.println(origi);
        /*final Measure<Double, Velocity> maxSpeed = Measure.valueOf(SPEED, dmas.rm.getSpeedUnit());
        Queue<Point> solution = new LinkedList(dmas.grm.getPathTo(origi, desti, SI.SECOND, maxSpeed, GeomHeuristics.euclidean()).getPath());*/
        while (!current.equals(desti)) {
            ArrayList<Point> roads = dmas.nodes.get(current).neighbors;
            while (true) {
                nextPoint = roads.get((int) (Math.random() * roads.size()));

                double deltaX = desti.x - current.x;
                double deltaY = desti.y - current.y;
                double distance1 = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                deltaX = desti.x - nextPoint.x;
                deltaY = desti.y - nextPoint.y;
                double distance2 = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (distance2 <= distance1*1.05) {
                    break;
                }
                if (Math.random() < 0.4)
                    break;
            }
            solution.add(nextPoint);
            current = nextPoint;
        }
        return solution;
    }
}
