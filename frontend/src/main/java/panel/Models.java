package panel;

import javax.swing.*;

import panel.model.*;

public class Models extends JTabbedPane {

    public Models() {
        addTab("Clients", new Clients());
        addTab("Vehicles", new Vehicles());
        addTab("Reservations", new Reservations());
        addTab("Users", new Users());
    }
}
