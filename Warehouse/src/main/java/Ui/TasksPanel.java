package Ui;

import DelgMas.AgvModel;
import DelgMas.Box;
import DelgMas.PheromoneStorage;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.Parcel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TasksPanel extends JFrame {

    public TaskListener taskListener;
    public AgvModel agvModel;
    JPanel infoPanel;
    private ArrayList<Box> store;

    public TasksPanel(AgvModel agvModel, ArrayList<Box> nodes) {
        super("Tasks View");
        this.store = nodes;
        this.agvModel = agvModel;

        infoPanel = new JPanel(new GridBagLayout());
        writePanel(infoPanel);

        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        this.add(scrollPane);
        this.pack();

        agvModel.setTaskListener(new TaskListener() {
            public void inputEmitted(ArrayList<Box> newstore) {
                store = newstore;
                updatePanel();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public void updatePanel() {
        this.infoPanel.removeAll();
        writePanel(this.infoPanel);
        this.infoPanel.revalidate();
        this.infoPanel.repaint();
    }

    public void writePanel(JPanel panel) {
        GridBagConstraints constraints = new GridBagConstraints();
        GridBagConstraints constraints2 = new GridBagConstraints();
        GridBagConstraints constraints3 = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;

        constraints2.anchor = GridBagConstraints.WEST;
        constraints2.insets = new Insets(2, 2, 2, 2);
        constraints2.gridx = 1;
        constraints2.gridy = 0;

        constraints3.anchor = GridBagConstraints.WEST;
        constraints3.insets = new Insets(2, 2, 2, 2);
        constraints3.gridx = 2;
        constraints3.gridy = 0;

        for (Parcel parcel : this.store) {
            constraints.gridy += 1;
            constraints2.gridy += 1;
            constraints3.gridy += 1;
            infoPanel.add(new JLabel("Position:" + parcel.getPickupLocation()), constraints);

            long begin = parcel.getPickupTimeWindow().begin();
            long end = parcel.getPickupTimeWindow().end();

            JLabel label1 = new JLabel("PickupTimeWindow: [" + begin +"," + end +"]");
            label1.setForeground(Color.RED);
            infoPanel.add(label1,constraints2);

            begin = parcel.getDeliveryTimeWindow().begin();
            end = parcel.getDeliveryTimeWindow().end();

            JLabel label2 = new JLabel("DeliverTimeWindow: [" + begin +"," + end +"]");
            label2.setForeground(Color.BLACK);

            infoPanel.add(label2,constraints3);
        }
    }
}

