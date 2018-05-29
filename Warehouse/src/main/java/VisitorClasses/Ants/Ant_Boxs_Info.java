package VisitorClasses.Ants;

import DelgMas.AgvModel;
import DelgMas.Box;
import VisitorClasses.Pheromones.Pheromone_A;
import VisitorClasses.Pheromones.Pheromone_B;
import VisitorClasses.Pheromones.Pheromone_C;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;

import java.util.ArrayList;
import java.util.Collection;

public class Ant_Boxs_Info extends Ant {
    public Ant_Boxs_Info(AgvModel agvModel) {
        super(agvModel);
    }

    @Override
    public void dropPheromone(Pheromone_A pheromone) {

    }

    @Override
    public int dropPheromone(Pheromone_B pheromone) {
        return 0;
    }
    @Override
    public void dropPheromone(Pheromone_C pheromone) {
        pheromone.boxes_info = new ArrayList<>(agv.getParcels(PDPModel.ParcelState.AVAILABLE));
    }
}