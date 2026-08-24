package gui;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.AcademicLevel;
import model.Specialty;
import model.Specialty;

/**
 * MainFrame
 */
public class MainFrame extends JFrame {

    public MainFrame() {

        super("WhatAreTheyWorth");

        var panel = new JPanel();

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
