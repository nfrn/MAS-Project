package DelgMas;

import VisitorClasses.Ants.*;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
import VisitorClasses.Visitable;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;
import java.util.Iterator;

public class PheromoneStorage implements Visitable {

    public static final int LIFETIME_A = 40;
    public static final int LIFETIME_B = 5;
    public static final int LIFETIME_C = 40;


    public ArrayList<Pheromone_A> list_phero_A;
    public ArrayList<Pheromone_Node_Booking> list_phero_B;
    public ArrayList<Pheromone_Boxes_Info> list_phero_C;
    public Point position;
    public ArrayList<Point> neighbors;

    public PheromoneStorage(Point point, ArrayList<Point> neighbors) {
        this.position = point;
        this.neighbors = neighbors;
        list_phero_A = new ArrayList<Pheromone_A>();
        list_phero_B = new ArrayList<Pheromone_Node_Booking>();
        list_phero_C = new ArrayList<Pheromone_Boxes_Info>();
    }

    public void time_passed() {

        Iterator<Pheromone_A> iter = list_phero_A.iterator();
        while (iter.hasNext()) {
            Pheromone_A pheA = iter.next();
            pheA.decreaseLifeTime();
            if (pheA.lifetime <= 0) {
                iter.remove();
            }
        }

        Iterator<Pheromone_Node_Booking> iter2 = list_phero_B.iterator();
        while (iter2.hasNext()) {
            Pheromone_Node_Booking pheB = iter2.next();
            pheB.decreaseLifeTime();
            if (pheB.lifetime <= 0) {
                iter2.remove();
            }
        }

        Iterator<Pheromone_Boxes_Info> iter3 = list_phero_C.iterator();
        while (iter3.hasNext()) {
            Pheromone_Boxes_Info pheC = iter3.next();
            pheC.decreaseLifeTime();
            if (pheC.lifetime <= 0) {
                iter3.remove();
            }
        }
    }

    @Override
    public int accept(Ant ant) {
        if (ant.getClass() == Ant_A.class) {
            //Go to chargers and see their availabiliy. Register that in Pheromone_A
            Pheromone_A pheromone_A = new Pheromone_A(LIFETIME_A, position);
            ant.dropPheromone(pheromone_A);
            boolean isthere = false;
            for (Pheromone_A phA : list_phero_A) {
                if (phA.position.equals(pheromone_A.position)) {
                    phA.chargers_booking1 = new ArrayList<>(pheromone_A.chargers_booking1);
                    phA.chargers_booking2 = new ArrayList<>(pheromone_A.chargers_booking2);
                    phA.chargers_booking3 = new ArrayList<>(pheromone_A.chargers_booking3);
                    phA.chargers_booking4 = new ArrayList<>(pheromone_A.chargers_booking4);
                    phA.lifetime = LIFETIME_A;
                    isthere = true;
                    break;
                }
            }
            if (!isthere) {
                list_phero_A.add(pheromone_A);
            }

        } else if (ant.getClass() == Ant_Intention.class) {
            Ant_Intention antB = (Ant_Intention) ant;
            //Pheromone_Node_Booking pheromone_B = new Pheromone_Node_Booking(LIFETIME_B,position, antB.getTimeWindow());
            for (Pheromone_Node_Booking pheromone_NodeBooking : list_phero_B) {
                int result = antB.dropPheromone(pheromone_NodeBooking);
                if (result == -1) {
                    return -1;
                }
            }

        } else if (ant.getClass() == Ant_Booking.class) {
            Ant_Booking antC = (Ant_Booking) ant;

            boolean isThere = false;
            for (Pheromone_Node_Booking phB : list_phero_B) {
                if (phB.getAgentID() == antC.getAgentID()) {
                    antC.dropPheromone(phB);
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                Pheromone_Node_Booking pheromone_NodeBooking = new Pheromone_Node_Booking(LIFETIME_B, position, antC.getTimeWindow(), antC.getAgentID());
                ant.dropPheromone(pheromone_NodeBooking);
                list_phero_B.add(pheromone_NodeBooking);
            }

        }
        else if (ant.getClass() == Ant_Boxs_Info.class) {
            Pheromone_Boxes_Info pheromone_BoxesInfo = new Pheromone_Boxes_Info(LIFETIME_A, position);
            ant.dropPheromone(pheromone_BoxesInfo);
            boolean isthere = false;
            for (Pheromone_Boxes_Info phC : list_phero_C) {
                if (phC.position.equals(pheromone_BoxesInfo.position)) {
                    phC.boxes_info = new ArrayList<>(pheromone_BoxesInfo.boxes_info);
                    phC.lifetime = LIFETIME_C;
                    isthere = true;
                    break;
                }
            }
            if (!isthere) {
                list_phero_C.add(pheromone_BoxesInfo);
            }
        }
            return 0;
    }



    public String getPheroAInfo() {
        String output = "";
        for (Pheromone_A phero : list_phero_A) {
            for (TimeWindow tw : phero.chargers_booking1) {
                output += "Charger1[" + tw.begin() + "," + tw.end() + "]:";
            }
            for (TimeWindow tw : phero.chargers_booking2) {
                output += "Charger2[" + tw.begin() + "," + tw.end() + "]:";
            }
            for (TimeWindow tw : phero.chargers_booking3) {
                output += "Charger3[" + tw.begin() + "," + tw.end() + "]:";
            }
            for (TimeWindow tw : phero.chargers_booking4) {
                output += "Charger4[" + tw.begin() + "," + tw.end() + "]:";
            }
        }

        return output;
    }

    public String getPheroBInfo() {
        String output = "";
        for (Pheromone_Node_Booking phero : list_phero_B) {
            TimeWindow tw = phero.node_booking;
            output += "[" + tw.begin() + "," + tw.end() + "]:";
        }

        return output;
    }
    public String getNeighborsInfo(){
        String output = "";
        for(Point pt: this.neighbors){
                output += "[" + pt.x +"," + pt.y + "]:";
        }
        return output;
    }

}
