package TaskAgent;

import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.SimulatorAPI;

import java.util.ArrayList;

public class Taxi_DMAS implements SimulatorUser {

    SimulatorAPI simulator;

    ArrayList<Exploration_Ant> exploration_ants;
    ArrayList<Intention_Ant> intention_ants;

    public Taxi_DMAS() {
        exploration_ants = new ArrayList<Exploration_Ant>();
        intention_ants = new ArrayList<Intention_Ant>();
    }

    public void realeaseExplorationAnts(){

    }

    public void realeaseIntentionAnts(){
    }

    public void setSimulator(SimulatorAPI simulator) {
        this.simulator = simulator;
    }
}
