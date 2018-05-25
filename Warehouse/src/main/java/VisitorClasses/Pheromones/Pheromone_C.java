package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;

public class Pheromone_C extends Pheromone {
    public ArrayList<ArrayList<Point>> paths;

    public Pheromone_C(int lifetime, Point point){

        super(lifetime,point);
        this.paths = new ArrayList<ArrayList<Point>>();
    }
}