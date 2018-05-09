package ResourceAgent;

import Ants.Feasibility.Feasibility_DMAS;
import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;

/**
 * A customer with very permissive time windows.
 */
public class Customer extends Parcel {

    Feasibility_DMAS dmas;

    public Customer(ParcelDTO dto, RoadModel rm, Simulator simulator, boolean mas_flag) {
        super(dto);
        if(mas_flag) {
            this.dmas = new Feasibility_DMAS(this);
            simulator.register(this.dmas);
        }
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {}



}