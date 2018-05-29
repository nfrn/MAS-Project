package DelgMas;

import VisitorClasses.Ants.Ant;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
import VisitorClasses.Pheromones.PheromoneConnectionBooking;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Visitable;
import com.github.rinde.rinsim.geom.Connection;
import com.github.rinde.rinsim.geom.Point;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Matija Kljun
 */
public class PheromoneConnectionStorage implements Visitable {

    public static final int LIFETIME = 50;

    public ArrayList<PheromoneConnectionBooking> list_phero_Connection_Booking;

    private Connection connection;

    public PheromoneConnectionStorage(Connection c) {
        this.connection = c;
        list_phero_Connection_Booking = new ArrayList<>();
    }

    public void time_passed() {
        Iterator<PheromoneConnectionBooking> iter2 = list_phero_Connection_Booking.iterator();
        while (iter2.hasNext()) {
            PheromoneConnectionBooking ph = iter2.next();
            ph.decreaseLifeTime();
            if (ph.lifetime <= 0) {
                iter2.remove();
            }
        }
    }

    @Override
    public int accept(Ant ant) {
        if (ant.getClass() == Ant_C.class) {
            // book the connection
            Ant_C antC = (Ant_C) ant;

            boolean isThere = false;
            for (PheromoneConnectionBooking ph : list_phero_Connection_Booking) {
                if (ph.getAgentID() == antC.getAgentID()) {
                    antC.dropPheromone(ph);
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                PheromoneConnectionBooking pheromone = new PheromoneConnectionBooking(LIFETIME, connection, antC.getTimeWindow(), antC.getAgentID());
                ant.dropPheromone(pheromone);
                list_phero_Connection_Booking.add(pheromone);
            }

        } else if(ant.getClass() == Ant_B.class) {
            // TODO check if the connection is booked . . .
            Ant_B antB = (Ant_B) ant;
            //Pheromone_B pheromone_B = new Pheromone_B(LIFETIME_B,position, antB.getTimeWindow());
            for (PheromoneConnectionBooking pheromone : list_phero_Connection_Booking) {
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
