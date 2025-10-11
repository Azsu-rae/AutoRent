package gui;

import javax.swing.*;
import java.awt.*;

import gui.component.*;
import gui.component.Factory.Field;

import static gui.component.Factory.createField;

import gui.contract.*;
import gui.contract.Listener.Event;

import orm.model.User;

class SignIn extends MyPanel {

    JTextField idField;
    JPasswordField passwordField;

    Listener listener;
    SignIn(Listener listener) {
        this.listener = listener;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID Label
        gbc.gridx = 0; gbc.gridy = 0;
        add(new MyLabel("Username or Email"), gbc);

        // ID Field
        idField = createField(15, Field.TEXT, 300, 40);
        gbc.gridx = 0; gbc.gridy = 1;
        add(idField, gbc);

        // Password Label
        gbc.gridx = 0; gbc.gridy = 3;
        add(new MyLabel("Password"), gbc);

        // Password Field
        passwordField = (JPasswordField) createField(15, Field.PASSWORD, 300, 40);
        gbc.insets = new Insets(5, 5, 15, 5);
        gbc.gridx = 0; gbc.gridy = 4;
        add(passwordField, gbc);

        // Login Button
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 5;
        add(new MyButton("Sign In", 300, 50, e -> loginAttempt()), gbc);
    }

    void loginAttempt() {
        if (User.authenticate(idField.getText(), String.valueOf(passwordField.getPassword()))) {
            idField.setText(""); passwordField.setText("");
            listener.onEvent(Event.LOG_IN);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!");
        }
    }
}
