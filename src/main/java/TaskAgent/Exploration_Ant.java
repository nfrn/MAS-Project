package TaskAgent;

import Core.Ant;
import com.github.rinde.rinsim.geom.Point;

import java.util.ArrayList;

public class Exploration_Ant extends Ant {

    public Exploration_Ant(){
        super(new Point(10,10), new ArrayList<Point>(),4);
    }

    public void smell(int p){
    }

    public void drop(int p){
    }

    public void evaporate(){
    }
}
