package org.pmf.alphaplus;

import org.pmf.alphaplus.model.PetriNet;
import org.pmf.alphaplus.ui.AlphaPetriNetGraph;

import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        AlphaP alphaP = new AlphaP("example-logs\\p108_log8.xes");
        PetriNet petriNet =  alphaP.getPetriNet();
        //  Show in Frame
        AlphaPetriNetGraph alphaPetriNetGraph = new AlphaPetriNetGraph(petriNet);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(alphaPetriNetGraph.createGraph()));
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
