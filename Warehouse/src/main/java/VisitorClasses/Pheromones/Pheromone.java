package VisitorClasses.Pheromones;

public class Pheromone {
    public static final int LIFE_DECREASE = 1;
    public int lifetime;

    public Pheromone(int lifetime){
        this.lifetime =lifetime;
    }

    public void decreaseLifeTime(){
        this.lifetime -= LIFE_DECREASE;
    }
}
