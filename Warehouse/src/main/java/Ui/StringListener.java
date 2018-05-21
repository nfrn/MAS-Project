package Ui;

import DelgMas.PheromoneStorage;
import VisitorClasses.Pheromones.Pheromone_A;

import java.util.ArrayList;

public interface StringListener {
    void inputEmitted(ArrayList<PheromoneStorage> nodes);
}