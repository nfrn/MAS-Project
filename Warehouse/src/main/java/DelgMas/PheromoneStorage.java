package DelgMas;

import com.github.rinde.rinsim.util.TimeWindow;
import java.util.ArrayList;

public class PheromoneStorage {
    public ArrayList<TimeWindow> booked;
    public ArrayList<TimeWindow> chargerSpots;
    public int lifetime;

    public PheromoneStorage(int lifetime){
        this.lifetime=lifetime;
        this.booked = new ArrayList<TimeWindow>();
        this.chargerSpots = new ArrayList<TimeWindow>(AgvExample.NUM_BATTERY);
    }

    public int booking(TimeWindow tw){
        for(TimeWindow tw1 : booked){
            if(tw1.end() < tw.begin() || tw1.begin() > tw.end()){
                this.booked.add(tw);
                System.out.println("Time window is possible. Booked!");
                return 1;
            }
        }
        System.out.println("Time window is not possible. Failed!");
        return -1;
    }

    public void setChargerSpots(int chargernumber, TimeWindow fulltw){
        this.chargerSpots.set(chargernumber,fulltw);
    }
}
