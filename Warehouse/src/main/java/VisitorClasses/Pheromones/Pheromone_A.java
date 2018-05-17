package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;

public class Pheromone_A extends Pheromone {
    public ArrayList<TimeWindow> chargers_booking;
    public Pheromone_A(int lifetime,Point point) {
        super(lifetime,point);
        chargers_booking= new ArrayList<TimeWindow>();
    }
}
