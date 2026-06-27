package gui.dashboard;

import javax.swing.JTabbedPane;
import gui.dashboard.record.Record;

public class Records extends JTabbedPane {
    public Records() {
        addTab("Clients", new Record("Client"));
        addTab("Vehicles", new Record("Vehicle"));
        addTab("Reservations", new Record("Reservation"));
        addTab("Users", new Record("User"));
    }
}
