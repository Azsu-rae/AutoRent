import bex.*;

import aex.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainApp {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel, sidebar;
    private JButton vehiculeButton, clientButton, reservationButton, userButton, mainButton;

    private JButton loginButton;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public MainApp() {

        loginSetup();
    }

    private void loginSetup() {

        frame = new JFrame("Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 700);

        LoginPanel loginPanel = new LoginPanel(frame);
        frame.add(loginPanel.getPanel());
        frame.setVisible(true);

        loginButton = loginPanel.getLoginButton(); 
        usernameField = loginPanel.getUsernameField(); 
        passwordField = loginPanel.getPasswordField(); 

        loginButton.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User u = UserInterface.checkLogin(username, password);
        if (u != null) {
            frame.getContentPane().removeAll();
            frame.revalidate();
            frame.repaint();
            initialize(u);
        } else {
            JOptionPane.showMessageDialog(frame, "Incorrect Email or Password.");
        }
    }

    private void initialize(User u) {

        sidebarSetup();
        mainPanelSetup();
        frameSetup();
    }

    private void frameSetup() {

        frame = new JFrame("Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 700);
        frame.setLayout(new BorderLayout());
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                AbstractTablePanel visiblePanel = null;
                Component[] cs = mainPanel.getComponents();
                for (Component c : cs) {
                    if (c.isVisible() && c instanceof AbstractTablePanel) {
                        visiblePanel = (AbstractTablePanel) c;
                        break;
                    }
                }

                JTable table = visiblePanel.getTable();
                if (!table.getBounds().contains(e.getPoint())) {
                    table.clearSelection();
                }
            }
        });

        frame.setVisible(true);
    }

    private void mainPanelSetup() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        VehiculePanel vehiculePanel = new VehiculePanel(); 
        mainPanel.add(vehiculePanel, "Vehicules");

        ClientPanel clientPanel = new ClientPanel();
        mainPanel.add(clientPanel, "Clients");

        ReservationPanel reservationPanel = new ReservationPanel();
        mainPanel.add(reservationPanel, "Reservations");

        UserPanel userPanel = new UserPanel();
        mainPanel.add(userPanel, "Users");

        vehiculeButton.addActionListener(e -> cardLayout.show(mainPanel, "Vehicules"));
        clientButton.addActionListener(e -> cardLayout.show(mainPanel, "Clients"));
        reservationButton.addActionListener(e -> cardLayout.show(mainPanel, "Reservations"));
        userButton.addActionListener(e -> cardLayout.show(mainPanel, "Users"));
    }

    private void sidebarSetup() {

        sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(150, 700));
        sidebar.add(btns(), BorderLayout.NORTH);
    }

    private JPanel btns() {

        vehiculeButton = new JButton("Vehicules");
        clientButton = new JButton("Clients");
        reservationButton = new JButton("Reservations");
        userButton = new JButton("Users");

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(4,1,5,5));
        btns.add(vehiculeButton);
        btns.add(clientButton);
        btns.add(reservationButton);
        btns.add(userButton);
        btns.setPreferredSize(new Dimension(150,200));

        return btns;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(MainApp::new);
    }
}
