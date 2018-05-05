package Core;

import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;

public class Pheromone implements TickListener {

    private int current_duration;

    public Pheromone(int initial_duration) {
        this.current_duration = initial_duration;
    }

    public int getCurrent_duration() {
        return current_duration;
    }

    public void setCurrent_duration(int current_duration){
        this.current_duration=current_duration;
    }

    public boolean is_evaporated(){
        if(this.current_duration < 1)
            return true;
        return false;
    }

    public void afterTick(TimeLapse timelapse) {}

    public void tick(TimeLapse timelapse) {
        this.current_duration -= timelapse.getEndTime()-timelapse.getStartTime();
    }

}
