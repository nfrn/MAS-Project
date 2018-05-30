package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.core.model.pdp.ParcelDTO;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import static DelgMas.AgvExample.TICK_LENGTH;

public class Battery extends Parcel {
    public static final int POWERLIMIT = 2000;
    public static final long CHARGING_DURATION = 10*TICK_LENGTH;
    public static final long DELIVER_TIME = 10*TICK_LENGTH;
    public long capacity;
    public Point destination;
    public TimeWindow timewindow;

    public Battery(Point position) {
        super(Parcel.builder(position,position).deliveryDuration(CHARGING_DURATION).buildDTO());
        this.capacity = POWERLIMIT;
    }

    public void setTimewindow(long begin) {
        this.timewindow = TimeWindow.create(begin, begin+DELIVER_TIME);
    }
}