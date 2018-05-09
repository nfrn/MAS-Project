package ResourceAgent;

import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.core.model.road.RoadModel;

public class Destination extends Depot {

    RoadModel rm;
    PDPModel pdp;


    public Destination(Point position) {
        super(position);
        setCapacity(1);
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        rm = pRoadModel;
        pdp = pPdpModel;
    }
}