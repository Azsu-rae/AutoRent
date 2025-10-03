package gui;

import javax.swing.*;
import java.awt.*;

import gui.util.*;
import orm.Table;
import gui.dashboard.Dashboard;

import static orm.util.Console.error;
import static orm.util.Database.*;

public class MainApp extends JFrame implements Listener {

    CardLayout cardLayout = new CardLayout();
    JPanel panels = new JPanel(cardLayout);

    MainApp() {

        // Setup
        super("AutoRent");
        Opts.useCustomTheme(false);
        Opts.setMainFrame(this);

        // Theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            error(e);
        }

        // Main Panels
        panels.add(new Dashboard(this), "Dashboard");
        panels.add(new SignIn(this), "Sign In");

        // JFrame settings
        setContentPane(panels);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

    public static void main(String[] args) {
        if (!Table.dbFile()) {
            readSampleData();
        } display();
        SwingUtilities.invokeLater(MainApp::new);
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
}
