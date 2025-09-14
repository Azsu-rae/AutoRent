package panel;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;

import ui.component.*;

public class Sidebar extends JPanel {

    Button dashboardBtn;
    Button vehiclesBtn;
    Button clientsBtn;
    Button reservationsBtn;

    public Sidebar() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        dashboardBtn = new Button("Dashboard");
        dashboardBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        vehiclesBtn = new Button("Vehicles");
        vehiclesBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        clientsBtn = new Button("Clients");
        clientsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        reservationsBtn = new Button("Reservations");
        reservationsBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        add(dashboardBtn);
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
