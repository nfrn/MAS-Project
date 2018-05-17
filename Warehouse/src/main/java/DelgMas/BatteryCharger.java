package DelgMas;

import com.github.rinde.rinsim.core.model.pdp.Depot;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.sql.Time;
import java.util.ArrayList;

public class BatteryCharger extends Depot {

    public static final int MAX_TIME=1000;
    Point position;
    ArrayList<TimeWindow> booked;

    public BatteryCharger(Point position) {
        super(position);
        this.position=position;
        this.booked = new ArrayList<TimeWindow>();
    }

    public int bookBatteryCharger(TimeWindow tw){
        long begin = tw.begin();
        long end = tw.end();
        for(TimeWindow tw1 : this.booked){
            if(!(tw1.isAfterEnd(begin) || (!tw1.isAfterStart(begin) && !tw1.isAfterStart(end)))){
                return -1;
            }
        }
        this.booked.add(tw);
        return 0;
    }

    public ArrayList<TimeWindow> getBooked() {
        return booked;
    }
}
