package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.DMASModel;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
import com.github.rinde.rinsim.geom.Point;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterMutableGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;

import java.util.*;

public class Ant_Path_Finder extends Ant {
    DMASModel dmas;
    Point desti;
    Point origi;
    HipsterMutableGraph graph = (HipsterMutableGraph) GraphBuilder.<Point, Double>create().createUndirectedGraph();

    public Ant_Path_Finder(AgvModel agvModel, DMASModel dmasModel, Point origi, Point desti) {
        super(agvModel);
        this.dmas = dmasModel;
        this.origi = origi;
        this.desti = desti;
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Node_Booking pheromone) {
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_Boxes_Info pheromone) {

    }

    public Queue<Point> getPath(int delay) {

        if (origi.equals(desti)) {
            return null;
        }

        Point nextPoint = origi;

        int result = 0;
        while (result < 150) {

            if(graph.add(nextPoint))
                result++;

            ArrayList<Point> attempt = dmas.nodes.get(nextPoint).neighbors;
            for (Point pt : attempt) {
                if(graph.add(pt))
                    result++;
                double dist = Point.distance(pt, nextPoint);
                graph.connect(nextPoint, pt, dist);
            }
            nextPoint = attempt.get((int) (Math.random() * attempt.size()));
        }

        SearchProblem p = GraphSearchProblem
                .startingFrom(origi)
                .in(graph)
                .takeCostsFromEdges()
                .build();
        if (delay != 0)

        {
            LinkedList solution = (LinkedList) Hipster.createDijkstra(p).search(desti).getOptimalPaths().get(0);
            if (delay < 25) {
                graph.remove(solution.get((int) (Math.random() * (solution.size()-1))));
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
