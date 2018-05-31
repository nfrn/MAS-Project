package VisitorClasses.Pheromones;

import com.github.rinde.rinsim.geom.Point;

public class Pheromone {
    public static final int LIFE_DECREASE = 1;
    public int lifetime;
    public Point position;

    public Pheromone(int lifetime,Point position){
        this.lifetime =lifetime;
        this.position = position;
    }

    public void decreaseLifeTime(){
        this.lifetime -= LIFE_DECREASE;
    }
}
