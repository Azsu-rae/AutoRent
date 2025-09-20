package panel;

import javax.swing.*;

import panel.table.Reservations;

public class Models extends JTabbedPane {

    public Models() {

        // Create tabbed pane
        JPanel reservationPanel = new Reservations();
        addTab("Reservations", reservationPanel);
        // Add other tabs (Vehicles, Clients, etc.) similarly
    }
}
