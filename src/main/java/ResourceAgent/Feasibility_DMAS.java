package ResourceAgent;

import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;

import java.util.Set;

public class Feasibility_DMAS implements TickListener {

    private Customer customer;

    private RoadModel rm;
    private Simulator simulator;

    private boolean initiated;

    public Feasibility_DMAS(Customer c, RoadModel rm, Simulator simulator) {
        this.customer = c;
        this.rm = rm;
        this.simulator = simulator;
        this.initiated = false;
    }

    public void Init_feasibility_ants() {
        Set<Customer> customers = rm.getObjectsOfType(Customer.class);

        for(Customer c : customers) {
            if(!this.customer.equals(c)) {
                double dist = rm.getDistanceOfPath(rm.getShortestPathTo(this.customer, rm.getPosition(c))).getValue();
                if (600 > dist) {

                    Feasibility_Ant ant = new Feasibility_Ant(rm.getPosition(c));
                    simulator.register(ant);
                }
            }
        }
        initiated = true;
    }

    @Override
    public void tick(TimeLapse timeLapse) {
        if(!initiated)
            Init_feasibility_ants();
    }

    @Override
    public void afterTick(TimeLapse timeLapse) {
        if(!initiated)
            Init_feasibility_ants();
    }
}
