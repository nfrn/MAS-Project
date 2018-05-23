package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;

public class Pheromone_C extends Pheromone {
    public ArrayList<Point> neighbors;

    public Pheromone_C(int lifetime, Point point){

        super(lifetime,point);
        this.neighbors = new ArrayList<Point>();
    }
}