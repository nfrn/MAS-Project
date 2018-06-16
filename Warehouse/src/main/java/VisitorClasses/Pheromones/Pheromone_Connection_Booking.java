package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

// the booking pheromone, contains data about node booking
public class Pheromone_Connection_Booking extends Pheromone {
    public TimeWindow connection_booking;
    // agv Agent that made the booking
    private int agentID;
    public Connection connection;

    public Pheromone_Connection_Booking(int lifetime, Connection conn, TimeWindow tm, int agentID){
        super(lifetime,conn.from());
        this.connection_booking = tm;
        this.agentID = agentID;
        this.connection = conn;
    }

    public int getAgentID() {
        return agentID;
    }
}
