package panel;

import java.awt.Dimension;

import javax.swing.*;

import ui.component.*;

public class Sidebar extends JPanel {

    MyButton homeBtn;
    MyButton vehiclesBtn;
    MyButton clientsBtn;
    MyButton reservationsBtn;

    public Sidebar() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        homeBtn = new MyButton("Home");
        homeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        vehiclesBtn = new MyButton("Vehicles");
        vehiclesBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        clientsBtn = new MyButton("Clients");
        clientsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        reservationsBtn = new MyButton("Reservations");
        reservationsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        add(homeBtn);
        add(Box.createVerticalStrut(10));
        add(vehiclesBtn);
        add(Box.createVerticalStrut(10));
        add(clientsBtn);
        add(Box.createVerticalStrut(10));
        add(reservationsBtn);

        add(Box.createVerticalGlue());

        setOpaque(false);
    }
}
