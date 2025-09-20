
import java.awt.*;
import javax.swing.*;

import orm.model.Reservation;
import panel.Dashboard;
import panel.SignIn;
import util.Listener;

import panel.table.AbstractTable;

import static orm.util.Database.*;

public class MainApp extends JFrame implements Listener {

    CardLayout cardLayout = new CardLayout();
    JPanel panels = new JPanel(cardLayout);

    MainApp() {

        super("AutoRent");
        panels.add(new Dashboard(this), "Dashboard");
        panels.add(new SignIn(this), "Login");

        setContentPane(panels);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    public static void main(String[] args) {

        readSampleData();
        display();

        SwingUtilities.invokeLater(MainApp::new);
    }

    @Override
    public void onEvent(Event e) {
        switch (e) {
            case LOG_IN:
                cardLayout.show(panels, "Dashboard");
                break;
            case LOG_OUT:
                cardLayout.show(panels, "Login");
                break;
            default:
                break;
        }
    }
}
