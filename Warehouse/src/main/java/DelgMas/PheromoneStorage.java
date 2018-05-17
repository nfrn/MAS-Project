package DelgMas;

import VisitorClasses.Ants.Ant;
import VisitorClasses.Ants.Ant_A;
import VisitorClasses.Ants.Ant_B;
import VisitorClasses.Ants.Ant_C;
import VisitorClasses.Pheromones.Pheromone;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import VisitorClasses.Visitable;
import com.github.rinde.rinsim.geom.Point;
import java.util.ArrayList;

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

    public void time_passed(){
        for(Pheromone_A pheA : this.list_phero_A){
            pheA.decreaseLifeTime();
        }
        for(Pheromone_B pheB : this.list_phero_B){
            pheB.decreaseLifeTime();
        }
        for(Pheromone_C pheC : this.list_phero_C){
            pheC.decreaseLifeTime();
        }
    }

    @Override
    public void accept(Ant ant) {
        if(ant.getClass()== Ant_A.class) {
            //Go to chargers and see their availabiliy. Register that in Pheromone_A
            Pheromone_A pheromone_A = new Pheromone_A(LIFETIME_A,position);
            ant.dropPheromone(pheromone_A);
            list_phero_A.add(pheromone_A);
        }else if(ant.getClass()== Ant_B.class) {
            Pheromone_B pheromone_B = new Pheromone_B(LIFETIME_B,position);
            ant.dropPheromone(pheromone_B);
            list_phero_B.add(pheromone_B);
        }else if(ant.getClass()== Ant_C.class) {
            Pheromone_C pheromone_C = new Pheromone_C(LIFETIME_C,position);
            ant.dropPheromone(pheromone_C);
            list_phero_C.add(pheromone_C);
        }
    }
}
