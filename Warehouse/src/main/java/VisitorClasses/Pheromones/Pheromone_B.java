package VisitorClasses.Pheromones;

import DelgMas.AgvAgent;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.sql.Time;
import java.util.ArrayList;

// the booking pheromone, contains data about node booking
public class Pheromone_B extends Pheromone {
    public TimeWindow node_booking;
    // agv Agent that made the booking
    private int agentID;

    public Pheromone_B(int lifetime, Point point, TimeWindow tm, int agentID){
        super(lifetime,point);
        this.node_booking = tm;
        this.agentID = agentID;
    }

    public int getAgentID() {
        return agentID;
    }
}
