package DelgMas;

import com.github.rinde.rinsim.util.TimeWindow;
import java.util.ArrayList;

public class PheromoneStorage {
    public ArrayList<TimeWindow> booked;
    public int lifetime_booking;
    public ArrayList<TimeWindow> chargerSpots;
    public int lifetime_charger;

    public PheromoneStorage(int lifetime){
        this.lifetime_booking=lifetime;
        this.lifetime_charger=lifetime;
        this.booked = new ArrayList<TimeWindow>();
        this.chargerSpots = new ArrayList<TimeWindow>(AgvExample.NUM_BATTERY);
    }

    public int booking(TimeWindow tw){
        for(TimeWindow tw1 : booked){
            if(tw1.end() < tw.begin() || tw1.begin() > tw.end()){
                this.booked.add(tw);
                this.lifetime_booking= 100;
                System.out.println("Time window is possible. Booked!");
                return 1;
            }
        }
        System.out.println("Time window is not possible. Failed!");
        return -1;
    }

    public void setChargerSpots(int chargernumber, TimeWindow fulltw){
        this.chargerSpots.set(chargernumber,fulltw);
        this.lifetime_charger= 100;
    }

    public int timepassing(int units){
        this.lifetime_booking-=units;
        this.lifetime_charger-=units;
        if (this.lifetime_booking < 0){
            this.booked.clear();
        }
        if (this.lifetime_charger < 0){
            this.chargerSpots.clear();
        }
        return 0;
    }
}
