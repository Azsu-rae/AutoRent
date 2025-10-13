package gui;

import javax.swing.*;
import java.awt.*;

import orm.Table;
import gui.dashboard.Dashboard;
import gui.contract.Listener;

import static orm.util.Database.*;

public class MainApp extends JFrame implements Listener {

    private CardLayout cardLayout = new CardLayout();
    private JPanel panels = new JPanel(cardLayout);

    private MainApp() {

        // Setup
        super("AutoRent");
        Opts.setMainFrame(this);

        // Main Panels
        panels.add(new Dashboard(this), "Dashboard");
        panels.add(new SignIn(this), "Sign In");

        // JFrame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panels);
        setSize(800, 600);
        setVisible(true);
    }

    @Override
    public void onEvent(Event e) {
        switch (e) {
            case LOG_IN:
                cardLayout.show(panels, "Dashboard");
                break;
            case LOG_OUT:
                cardLayout.show(panels, "Sign In");
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        if (!Table.dbFile()) {
            readSampleData();
        } new MainApp();
    }
}
