package ResourceAgent;

import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.geom.Point;

public class CrossRoad extends Depot {

    RoadModel rm;
    PDPModel pdp;


    public CrossRoad(Point position) {
        super(position);
        setCapacity(1);
    }

    @Override
    public void initRoadPDP(RoadModel pRoadModel, PDPModel pPdpModel) {
        rm = pRoadModel;
        pdp = pPdpModel;
    }
}