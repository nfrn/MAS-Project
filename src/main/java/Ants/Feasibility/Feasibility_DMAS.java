package Ants.Feasibility;

import ResourceAgent.Customer;
import com.github.rinde.rinsim.core.Simulator;
import com.github.rinde.rinsim.core.SimulatorAPI;
import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;

import java.util.Set;

public class Feasibility_DMAS implements TickListener, RoadUser, SimulatorUser {

    public static final int LIFETIME = 4000;
    private Customer customer;

    private RoadModel rm;
    private SimulatorAPI simulator;

    private boolean initiated;

    public Feasibility_DMAS(Customer c) {
        this.customer = c;
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

    @Override
    public void setSimulator(SimulatorAPI simulatorAPI) {
        this.simulator = simulatorAPI;
    }

    @Override
    public void initRoadUser(RoadModel roadModel) {
        this.rm = roadModel;
    }
}
