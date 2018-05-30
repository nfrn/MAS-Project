package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import static DelgMas.AgvExample.TICK_LENGTH;

public class Box extends Parcel implements TickListener {
    private static final long  MAX_CAPACITY= 1;
    public static final long SERVICE_DURATION = 0;//50*TICK_LENGTH;
    public static final long PICKUP_MAX_TIME = 30*TICK_LENGTH;
    public static final long DELIVER_MAX_TIME = 50*TICK_LENGTH;
    public static final long DECREASE_STORAGE_TIME = 1;

    public static final int MIN_STORAGE_TIME = (int) (0.1 * TICK_LENGTH);
    public static final int MAX_STORAGE_TIME = (int) (0.1 * TICK_LENGTH);
    boolean finaldestination;
    public boolean isAvailable;
    private long storageTime;

    public Box(Point ori, Point dest, long createdTime,boolean finaldestination) {

        super(Parcel.builder(ori,
               dest)
                .neededCapacity(MAX_CAPACITY)
                .pickupTimeWindow(TimeWindow.create(createdTime,createdTime+ PICKUP_MAX_TIME))
                .deliveryTimeWindow(TimeWindow.create(createdTime,createdTime + DELIVER_MAX_TIME))
                .pickupDuration(SERVICE_DURATION)
                .buildDTO());

        this.isAvailable = true;
        this.finaldestination = finaldestination;
    }

    public Box(Point ori, Point dest, long createdTime,boolean finaldestination, long storageTime) {

        super(Parcel.builder(ori,
                dest)
                .neededCapacity(MAX_CAPACITY)
                .pickupTimeWindow(TimeWindow.create(createdTime,createdTime+ PICKUP_MAX_TIME))
                .deliveryTimeWindow(TimeWindow.create(createdTime,createdTime + DELIVER_MAX_TIME))
                .pickupDuration(SERVICE_DURATION)
                .buildDTO());

        this.isAvailable = false;
        this.finaldestination = finaldestination;
        this.storageTime = MIN_STORAGE_TIME + storageTime;

    }

    public long getStorageTime() {
        return storageTime;
    }

    @Override
    public void tick(TimeLapse timeLapse) {
        if (!this.isAvailable) {
            this.storageTime -= DECREASE_STORAGE_TIME;
        }

        if (this.storageTime < 0)
            this.isAvailable = true;
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {

    }
}