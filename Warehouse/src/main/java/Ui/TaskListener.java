package Ui;

import DelgMas.Box;
import DelgMas.PheromoneStorage;
import com.github.rinde.rinsim.core.model.pdp.Parcel;

import java.util.ArrayList;

public interface TaskListener {
    void inputEmitted(ArrayList<Box> taskes);
}
