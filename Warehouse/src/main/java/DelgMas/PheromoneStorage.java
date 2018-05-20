package DelgMas;

import VisitorClasses.Ants.Ant;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import VisitorClasses.Visitable;
import com.github.rinde.rinsim.geom.Point;

import java.util.ArrayList;
import java.util.Iterator;

public class PheromoneStorage implements Visitable {

    public static final int LIFETIME_A=100;
    public static final int LIFETIME_B=100;
    public static final int LIFETIME_C=100;


    ArrayList<Pheromone_A> list_phero_A;
    ArrayList<Pheromone_B> list_phero_B;
    ArrayList<Pheromone_C> list_phero_C;
    Point position;

    public PheromoneStorage(Point point){
        this.position=point;
        list_phero_A = new ArrayList<Pheromone_A>();
        list_phero_B = new ArrayList<Pheromone_B>();
        list_phero_C = new ArrayList<Pheromone_C>();
    }

    public void time_passed() {
        Iterator<Pheromone_A> iter = list_phero_A.iterator();
        while (iter.hasNext()) {
            Pheromone_A pheA = iter.next();

            if (pheA.lifetime < 0) {
                iter.remove();
            }
            Iterator<Pheromone_B> iter2 = list_phero_B.iterator();
            while (iter2.hasNext()) {
                Pheromone_B pheB = iter2.next();

                if (pheB.lifetime < 0) {
                    iter2.remove();
                }
            }
            Iterator<Pheromone_C> iter3 = list_phero_C.iterator();
            while (iter3.hasNext()) {
                Pheromone_C pheC = iter3.next();
                if (pheC.lifetime < 0) {
                    iter3.remove();
                }
            }
        }
    }

    @Override
    public int accept(Ant ant) {
        if(ant.getClass()== Ant_A.class) {
            //Go to chargers and see their availabiliy. Register that in Pheromone_A
            Pheromone_A pheromone_A = new Pheromone_A(LIFETIME_A,position);
            ant.dropPheromone(pheromone_A);
            list_phero_A.add(pheromone_A);
        }else if(ant.getClass()== Ant_B.class) {
            Pheromone_B pheromone_B = new Pheromone_B(LIFETIME_B,position);
            int result = ant.dropPheromone(pheromone_B);
            if(result==-1){
                return -1;
            }
            list_phero_B.add(pheromone_B);
        }else if(ant.getClass()== Ant_C.class) {
            Pheromone_C pheromone_C = new Pheromone_C(LIFETIME_C,position);
            ant.dropPheromone(pheromone_C);
            list_phero_C.add(pheromone_C);
        }
        return 0;
    }
}
