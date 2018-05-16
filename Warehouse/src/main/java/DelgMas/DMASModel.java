package demo.DMAS;

import com.github.rinde.rinsim.core.SimulatorAPI;
import com.github.rinde.rinsim.core.SimulatorUser;
import com.github.rinde.rinsim.core.model.time.TickListener;
import com.github.rinde.rinsim.core.model.time.TimeLapse;
import com.github.rinde.rinsim.geom.Graph;
import com.github.rinde.rinsim.geom.Point;
import demo.PheromoneStorage;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.Map;

/**
 * @author Matija Kljun
 */
public class DMASModel implements SimulatorUser, TickListener {

    private SimulatorAPI simulator;
    private Graph g;

    private Map<Point, PheromoneStorage> pheromones;

    public DMASModel(SimulatorAPI simulator, Map<Point, PheromoneStorage> pheromones) {
        this.pheromones = pheromones;
        this.simulator = simulator;
    }



    @Override
    public void tick(TimeLapse timeLapse) {

    }

    @Override
    public void afterTick(TimeLapse timeLapse) {

    }

    @Override
    public void setSimulator(SimulatorAPI simulatorAPI) {
        simulator = simulatorAPI;
    }
}
