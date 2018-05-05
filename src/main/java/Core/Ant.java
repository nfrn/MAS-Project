package Core;

import com.github.rinde.rinsim.core.model.pdp.Vehicle;
import com.github.rinde.rinsim.core.model.pdp.VehicleDTO;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Point;

import java.util.ArrayList;

public class Ant extends Vehicle {

    private static final double SPEED = 1000d;
    private ArrayList<Point> placesToGo;

    public Ant(Point start_position, ArrayList<Point> placesToGo, int pheromone_capacity){
        super(VehicleDTO.builder()
                .capacity(pheromone_capacity)
                .startPosition(start_position)
                .speed(SPEED)
                .build());

        this.placesToGo=placesToGo;

    }

    public void afterTick(TimeLapse timelapse) {}

    public void tickImpl(TimeLapse timelapse) {

    }
}
