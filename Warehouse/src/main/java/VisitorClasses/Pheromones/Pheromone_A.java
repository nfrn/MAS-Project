package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;

public class Pheromone_A extends Pheromone {
    public ArrayList<TimeWindow> chargers_booking1;
    public ArrayList<TimeWindow> chargers_booking2;
    public ArrayList<TimeWindow> chargers_booking3;
    public ArrayList<TimeWindow> chargers_booking4;
    public Pheromone_A(int lifetime,Point point) {
        super(lifetime,point);
        chargers_booking1= new ArrayList<TimeWindow>();
        chargers_booking2= new ArrayList<TimeWindow>();
        chargers_booking3= new ArrayList<TimeWindow>();
        chargers_booking4= new ArrayList<TimeWindow>();
    }
}
