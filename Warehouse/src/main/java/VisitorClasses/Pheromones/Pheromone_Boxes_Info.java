package VisitorClasses.Pheromones;

import DelgMas.Box;
import com.github.rinde.rinsim.core.model.pdp.Parcel;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.util.TimeWindow;

import java.util.ArrayList;

public class Pheromone_Boxes_Info extends Pheromone {
    public ArrayList<Parcel> boxes_info;

    public Pheromone_Boxes_Info(int lifetime, Point point){

        super(lifetime,point);
        this.boxes_info = new ArrayList<Parcel>();
    }
}