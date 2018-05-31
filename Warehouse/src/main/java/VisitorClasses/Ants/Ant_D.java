package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.DMASModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.geom.GeomHeuristics;
import com.github.rinde.rinsim.geom.Point;
import edu.uci.ics.jung.graph.event.GraphEvent;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.graph.HipsterMutableGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;

import javax.measure.Measure;
import javax.measure.quantity.Velocity;
import javax.measure.unit.SI;
import java.util.*;

import static DelgMas.AgvAgent.SPEED;

public class Ant_D extends Ant {
    DMASModel dmas;
    Point desti;
    Point origi;
    HipsterMutableGraph graph =(HipsterMutableGraph) GraphBuilder.<Point,Double>create().createUndirectedGraph();

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

        Point nextPoint = origi;

        while(true){

            graph.add(nextPoint);
            ArrayList<Point> attempt = dmas.nodes.get(nextPoint).neighbors;
            for(Point pt : attempt) {
                graph.add(pt);
                double dist = Math.sqrt(Math.pow(pt.x - nextPoint.x, 2) + Math.pow(pt.y - nextPoint.y, 2));
                graph.connect(nextPoint, pt, dist);
            }
            nextPoint = attempt.get((int)(Math.random()*attempt.size()));
            if(nextPoint.equals(desti)){
                break;
            }
        }


        SearchProblem p = GraphSearchProblem
                .startingFrom(origi)
                .in(graph)
                .takeCostsFromEdges()
                .build();
        if(delay!=0) {
            LinkedList solution = (LinkedList) Hipster.createDijkstra(p).search(desti).getOptimalPaths().get(0);
            if(delay<25){
                graph.remove(solution.get((int)(Math.random()*solution.size())));
            }else{
                graph.remove(solution.get(0));
            }
        }

        SearchProblem p2 = GraphSearchProblem
                .startingFrom(origi)
                .in(graph)
                .takeCostsFromEdges()
                .build();

        LinkedList solution2 = (LinkedList) Hipster.createDijkstra(p2).search(desti).getOptimalPaths().get(0);
        solution2.remove(0);

        return solution2;

    }
}
