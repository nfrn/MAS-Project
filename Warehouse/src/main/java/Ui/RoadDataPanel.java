package Ui;

import DelgMas.DMASModel;
import DelgMas.PheromoneStorage;
import VisitorClasses.Pheromones.Pheromone_A;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;

public class RoadDataPanel extends JFrame implements EventListener {
    private ArrayList<PheromoneStorage> store;
    JPanel infoPanel;
    final int size;
    public StringListener stringListener;
    public DMASModel dmasModel;

    public RoadDataPanel(DMASModel dmasModel,ArrayList<PheromoneStorage> nodes){
        super("PheromoneStorage View");
        this.store=nodes;
        this.dmasModel=dmasModel;
        this.size = nodes.size();
        System.out.println(size);

        infoPanel = new JPanel(new GridBagLayout());
        writePanel(infoPanel);

        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(500,700));

        this.add(scrollPane);
        this.pack();

        dmasModel.setStringListener(new StringListener() {
            public void inputEmitted(ArrayList<PheromoneStorage> newstore) {
                store = newstore;
                updatePanel();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public void updatePanel(){
        this.infoPanel.removeAll();
        writePanel(this.infoPanel);
        this.infoPanel.revalidate();
        this.infoPanel.repaint();
    }

    public void writePanel(JPanel panel){
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

        for(PheromoneStorage store: this.store){
            constraints.gridy +=1;
            constraints2.gridy +=1;
            constraints3.gridy +=1;
            infoPanel.add(new JLabel("Position"+ store.position),constraints);

            String output = store.getPheroAInfo();
            if(output.equals("")){
                output = "[            ]";
            }

            JLabel label1 = new JLabel("Charger Booking:"+ output);
            label1.setForeground(Color.RED);

            infoPanel.add(label1,constraints2);
            String output2 = store.getPheroCInfo();
            if(output2.equals("")){
                output2 = "[            ]";
            }

            JLabel label2 = new JLabel("Node Booking:"+ output2);
            label2.setForeground(Color.BLACK);

            infoPanel.add(label2,constraints3);
        }
    }
}
