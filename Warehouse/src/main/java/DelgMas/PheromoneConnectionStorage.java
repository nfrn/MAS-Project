package DelgMas;

import VisitorClasses.Ants.Ant;
import VisitorClasses.Ants.Ant_Intention;
import VisitorClasses.Ants.Ant_Booking;
import VisitorClasses.Pheromones.Pheromone_Connection_Booking;
import VisitorClasses.Visitable;
import com.github.rinde.rinsim.geom.Connection;

import java.util.ArrayList;
import java.util.Iterator;


public class PheromoneConnectionStorage implements Visitable {

    public static final int LIFETIME = PheromoneStorage.LIFETIME_B;

    public ArrayList<Pheromone_Connection_Booking> list_phero_Connection_Booking;

    private Connection connection;

    public PheromoneConnectionStorage(Connection c) {
        this.connection = c;
        list_phero_Connection_Booking = new ArrayList<>();
    }

    public void time_passed() {
        Iterator<Pheromone_Connection_Booking> iter2 = list_phero_Connection_Booking.iterator();
        while (iter2.hasNext()) {
            Pheromone_Connection_Booking ph = iter2.next();
            ph.decreaseLifeTime();
            if (ph.lifetime <= 0) {
                iter2.remove();
            }
        }
    }

    @Override
    public int accept(Ant ant) {
        if (ant.getClass() == Ant_Booking.class) {
            // book the connection
            Ant_Booking antC = (Ant_Booking) ant;

            boolean isThere = false;
            for (Pheromone_Connection_Booking ph : list_phero_Connection_Booking) {
                if (ph.getAgentID() == antC.getAgentID()) {
                    antC.dropPheromone(ph);
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                Pheromone_Connection_Booking pheromone = new Pheromone_Connection_Booking(LIFETIME, connection, antC.getTimeWindow(), antC.getAgentID());
                ant.dropPheromone(pheromone);
                list_phero_Connection_Booking.add(pheromone);
            }

        } else if(ant.getClass() == Ant_Intention.class) {
            Ant_Intention antB = (Ant_Intention) ant;
            //Pheromone_Node_Booking pheromone_B = new Pheromone_Node_Booking(LIFETIME_B,position, antB.getTimeWindow());
            for (Pheromone_Connection_Booking pheromone : list_phero_Connection_Booking) {
                int result = antB.dropPheromone(pheromone);
                if (result == -1) {
                    return -1;
                }
            }
        }
        return 0;
    }

    public Connection getConnection() {
        return connection;
    }
}
