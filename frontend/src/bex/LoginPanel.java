package bex;

import javax.swing.*;
import java.awt.*;

public class LoginPanel {

    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPanel(JFrame frame) {

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 32, 55)); 

        // Title Label
        JLabel titleLabel = new JLabel("AAAG car RENT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add padding around the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(36, 42, 67)); // Slightly lighter background
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form fields
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around fields

        JLabel usernameLabel = new JLabel("Email:");
        usernameLabel.setForeground(Color.WHITE); // White text color
        usernameField = new JTextField(15);
        Styling.styleTextLabel(usernameLabel);
        Styling.styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE); // White text color
        passwordField = new JPasswordField(15);
        Styling.styleTextLabel(passwordLabel);
        Styling.styleTextField(passwordField);

        loginButton = new JButton("Login");
        Styling.styleButton(loginButton);

        // Set the login button as the default button
        frame.getRootPane().setDefaultButton(loginButton);

        // Add components to the form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        gbc.gridy = 4;
        formPanel.add(loginButton, gbc);

        // Add components to the main panel
        panel.add(Box.createVerticalStrut(20)); // Top spacing
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20)); // Spacing between title and form
        panel.add(formPanel);
        panel.add(Box.createVerticalStrut(20)); // Bottom spacing
    }

    public JPanel getPanel() {

        return panel;
    }

    public JButton getLoginButton() {

        return loginButton;
    }

    public JTextField getUsernameField() {
        
        return usernameField;
    }

    public JPasswordField getPasswordField() {

        return passwordField;
    }
}
