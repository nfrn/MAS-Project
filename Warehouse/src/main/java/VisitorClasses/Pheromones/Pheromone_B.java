package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.sql.Time;
import java.util.ArrayList;

public class Pheromone_B extends Pheromone {
    public ArrayList<TimeWindow> node_booking;

    public Pheromone_B(int lifetime, Point point){

        super(lifetime,point);
        this.node_booking = new ArrayList<TimeWindow>();
    }
}
