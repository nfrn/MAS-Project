package Ants.Feasibility;

import Core.Pheromone;
import ResourceAgent.Customer;

public class Feasibility_Pheromone extends Pheromone {

    public Customer origin;
    public int distance;

    public Feasibility_Pheromone(Customer origin, int distance){
        super(Feasibility_DMAS.LIFETIME);
        this.origin = origin;
        this.distance = distance;
    }
}
