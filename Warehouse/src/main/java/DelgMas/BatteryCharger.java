package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.geom.Point;

public class BatteryCharger extends Depot {

    Point position;
    public BatteryCharger(Point position) {
        super(position);
        this.position=position;
    }
}
