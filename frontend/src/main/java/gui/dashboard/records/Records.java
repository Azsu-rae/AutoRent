package gui.dashboard.records;

import javax.swing.JTabbedPane;

public class Records extends JTabbedPane {

    public Records() {
        addTab("Clients", new Clients());
        addTab("Vehicles", new Vehicles());
        addTab("Reservations", new Reservations());
        addTab("Users", new Users());
    }
}
