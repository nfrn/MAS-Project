package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.sql.Time;
import java.util.ArrayList;

public class BatteryCharger extends Depot {

    public static final int MAX_TIME=1000;
    public Point position;
    private ArrayList<TimeWindow> booking;

    public BatteryCharger(Point position) {
        super(position);
        this.position=position;
        this.booking = new ArrayList<TimeWindow>();
    }

    public int bookBatteryCharger(TimeWindow tw){
        long begin = tw.begin();
        long end = tw.end();
        for(TimeWindow tw1 : this.booking){
            if(!(tw1.isAfterEnd(begin) || (!tw1.isAfterStart(begin) && !tw1.isAfterStart(end)))){
                return -1;
            }
        }
        this.booking.add(tw);
        return 0;
    }

    public boolean checkBooking(TimeWindow tw) {
        long begin = tw.begin();
        long end = tw.end();
        for(TimeWindow tw1 : this.booking){
            if(tw1.isIn(begin) || tw1.isIn(end)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<TimeWindow> getBooking() {
        return booking;
    }
}
