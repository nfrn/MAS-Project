package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.BatteryCharger;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.geom.Point;

public class Ant_A extends Ant{
    public Ant_A(AgvModel agvModel) {
        super(agvModel);
    }
    //Now we need to implement what to do to each pheromone along the way

    @Override
    public void dropPheromone(Pheromone_A pheromone) {
        for(BatteryCharger batteryCharger: agv.getRoadModel().getObjectsOfType(BatteryCharger.class)){
            if(agv.getRoadModel().getPosition(batteryCharger).equals(new Point(36,4))){
                pheromone.chargers_booking1 = batteryCharger.getBooking();
            }
            else if(agv.getRoadModel().getPosition(batteryCharger).equals(new Point(40,4))){
                pheromone.chargers_booking2 = batteryCharger.getBooking();
            }
            else if(agv.getRoadModel().getPosition(batteryCharger).equals(new Point(36,44))){
                pheromone.chargers_booking3 = batteryCharger.getBooking();
            }
            else if(agv.getRoadModel().getPosition(batteryCharger).equals(new Point(40,44))){
                pheromone.chargers_booking4 = batteryCharger.getBooking();
            }
        }
    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        return 0;
    }

    @Override
    public void dropPheromone(Pheromone_C pheromone) {
    }
}
