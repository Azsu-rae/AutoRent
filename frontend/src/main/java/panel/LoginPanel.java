package panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.*;

import component.Button;
import component.Label;

import util.*;
import util.Listener.Event;
import orm.model.User;

import static component.Factory.*;

public class LoginPanel extends JPanel implements Source {

    public LoginPanel(Listener listener) {

        setLayout(new GridBagLayout());
        setBackground(new Color(25, 32, 55)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new Label("Email:"), gbc);

        // Email Text Field
        JTextField emailField = field(15, Field.TEXT);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new Label("Password:"), gbc);

        // Password Field
        JPasswordField passwordField = (JPasswordField) field(15, Field.PASSWORD);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Login Button (spans 2 columns)
        Button loginBtn = new Button("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            if (User.authenticate(emailField.getText(), passwordField.getPassword())) {
                notifyListener(listener);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login!");
            }
        });
    }

    @Override
    public void notifyListener(Listener listener) {
        listener.onEvent(Event.LOG_IN);
    }
}
