package ResourceAgent;

import Ants.Feasibility.Feasibility_DMAS;
import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.geom.Point;

/**
 * A customer with very permissive time windows.
 */
public class Customer extends Parcel{

    public Feasibility_DMAS dmas;
    public Destination destination;
    RoadModel rm;
    PDPModel pdp;
    Simulator sm;

    public Customer(ParcelDTO dto, RoadModel rm, Simulator simulator, boolean mas_flag) {
        super(dto);
        this.sm = simulator;
        this.destination = new Destination(new Point(dto.getDeliveryLocation().x, dto.getDeliveryLocation().y));
        simulator.register(this.destination);
        if(mas_flag) {
            this.dmas = new Feasibility_DMAS(this);
            simulator.register(this.dmas);
        }
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        rm = pRoadModel;
        pdp = pPdpModel;
    }

    public void remove() {
        sm.unregister(this.dmas);
        sm.unregister(this.destination);

    }
}