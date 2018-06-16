package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.Box;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_Node_Booking;
import VisitorClasses.Pheromones.Pheromone_Boxes_Info;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;

import java.util.ArrayList;

public class Ant_Boxs_Info extends Ant {
    public Ant_Boxs_Info(AgvModel agvModel) {
        super(agvModel);
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_Node_Booking pheromone) {
        return 0;
    }
    @Override
    public void dropPheromone(Pheromone_Boxes_Info pheromone) {
        pheromone.boxes_info = new ArrayList<>();
        for(Parcel parcel : agv.getParcels(PDPModel.ParcelState.AVAILABLE)){
            if(parcel.getClass().equals(Box.class)){
                pheromone.boxes_info.add(parcel);
            }
        }
    }
}
