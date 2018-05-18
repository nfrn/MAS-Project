package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.BatteryCharger;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.core.model.road.RoadModels;
import com.github.rinde.rinsim.core.model.road.RoadUser;

public class Ant_A extends Ant{
    public Ant_A(AgvModel agvModel) {
        super(agvModel);
    }
    //Now we need to implement what to do to each pheromone along the way

    @Override
    public void dropPheromone(Pheromone_A pheromone) {
        BatteryCharger bc = RoadModels.findClosestObject(pheromone.position,agv.getRoadModel(),BatteryCharger.class);
        pheromone.chargers_booking = bc.getBooked();
        //System.out.println(pheromone.position + "knows that the closest charger is booked:" + pheromone.chargers_booking);
    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
    }
}
