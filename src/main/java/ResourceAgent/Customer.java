package ResourceAgent;

import TaskAgent.Intention_Ant;
import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;

import java.util.ArrayList;

/**
 * A customer with very permissive time windows.
 */
public class Customer extends Parcel {
    int current_pheremone;
    ArrayList<Intention_Ant> intention_ants;

    Feasibility_DMAS dmas;

    public Customer(ParcelDTO dto, RoadModel rm, Simulator simulator, boolean mas_flag) {
        super(dto);
        current_pheremone = 0;
        intention_ants = new ArrayList<Intention_Ant>();

        if(mas_flag) {
            this.dmas = new Feasibility_DMAS(this, rm, simulator);
            simulator.register(this.dmas);
        }
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {}


    public void realeaseFeasibilityAnts(){
    }
}