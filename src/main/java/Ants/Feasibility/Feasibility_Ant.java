package Ants.Feasibility;

import Core.Ant;
import ResourceAgent.Customer;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;

import java.util.ArrayList;
import java.util.Set;

public class Feasibility_Ant extends Ant {

    private RoadModel rm;
    private Customer customer;

    public Feasibility_Ant(Point startpostion){
        super(startpostion, new ArrayList<Point>(),4);
    }


    public void smell(int p){
    }

    public void drop(int p){
    }

    public void evaporate(){
    }

}
