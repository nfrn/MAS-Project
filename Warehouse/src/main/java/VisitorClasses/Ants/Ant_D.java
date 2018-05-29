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
import java.util.Comparator;
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

    public Queue<Point> getPath(int delay) {

        if(origi.equals(desti)){
            return new LinkedList<>();
        }

        int loops = agv.getRoadModel().getShortestPathTo(origi,desti).size();

        Queue<Point> currSolution;
        double currDist;
        Point nextPoint = origi;


        do{
            currSolution = new LinkedList();
            nextPoint = origi;
            while (!nextPoint.equals(desti)) {
                ArrayList<Point> roads = dmas.nodes.get(nextPoint).neighbors;
                nextPoint = roads.get((int) (Math.random() * (roads.size())));
                currSolution.add(nextPoint);
                if(currSolution.size()>loops+delay){
                    currSolution = null;
                    break;
                }
            }
        }while(currSolution==null);

        System.out.println(currSolution.size()+ " vs:" + (agv.getRoadModel().getShortestPathTo(origi,desti).size()-1));
        return currSolution;
    }
}
