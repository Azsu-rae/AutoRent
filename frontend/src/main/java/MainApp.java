
import java.awt.*;
import javax.swing.*;

import panel.Dashboard;
import panel.SignIn;

import util.Listener;
import util.Opts;

import static orm.util.Console.*;

import static orm.util.Database.*;

public class MainApp extends JFrame implements Listener {

    CardLayout cardLayout = new CardLayout();
    JPanel panels = new JPanel(cardLayout);

    MainApp() {

        // Setup
        super("AutoRent");
        Opts.useCustomTheme(false);
        Opts.setMainFrame(this);
        try {
            // TODO Look into this
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            error(e);
        }

        // Main Panels
        panels.add(new Dashboard(this), "Dashboard");
        panels.add(new SignIn(this), "Login");

        // JFrame settings
        setContentPane(panels);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
    }

    public static void main(String[] args) {

//        readSampleData();
//        clear();
//        display();

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
