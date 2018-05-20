package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.core.model.pdp.TimeWindowPolicy;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import static DelgMas.AgvExample.TICK_LENGTH;

public class Box extends Parcel {
    private static final long  MAX_CAPACITY= 1;
    public static final long SERVICE_DURATION = 10*TICK_LENGTH;
    public static final long PICKUP_MAX_TIME = 300*TICK_LENGTH;
    public static final long DELIVER_MAX_TIME = 500*TICK_LENGTH;
    boolean finaldestination;

    public Box(Point ori, Point dest, long createdTime,boolean finaldestination) {

        super(Parcel.builder(ori,
               dest)
                .neededCapacity(MAX_CAPACITY)
                .pickupTimeWindow(TimeWindow.create(createdTime,createdTime+ PICKUP_MAX_TIME))
                .deliveryTimeWindow(TimeWindow.create(createdTime,createdTime + DELIVER_MAX_TIME))
                .pickupDuration(SERVICE_DURATION)
                .buildDTO());

        this.finaldestination = finaldestination;
    }
}