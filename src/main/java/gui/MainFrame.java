package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * MainFrame
 */
public class MainFrame extends JFrame {

    public MainFrame() {

        super("WhatAreTheyWorth");

        JLabel label = new JLabel("What are they worth?");
        add(label);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
