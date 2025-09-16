package panel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import ui.component.*;

import util.*;
import util.Listener.Event;

import orm.model.User;

import static ui.Factory.*;

public class SignIn extends MyPanel implements Source {

    public SignIn(Listener listener) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Label
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        add(new MyLabel("Username or Email"), gbc);

        // ID Field
        JTextField idField = field(15, Field.TEXT);
        idField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 0; gbc.gridy = 1;
        add(idField, gbc);

        // Password Label
        gbc.gridx = 0; gbc.gridy = 3;
        add(new MyLabel("Password"), gbc);

        // Password Field
        JPasswordField passwordField = (JPasswordField) field(15, Field.PASSWORD);
        passwordField.setPreferredSize(new Dimension(300, 40));
        gbc.insets = new Insets(5, 5, 15, 5);
        gbc.gridx = 0; gbc.gridy = 4;
        add(passwordField, gbc);

        // Login Button
        MyButton loginBtn = new MyButton("Sign in");
        loginBtn.setPreferredSize(new Dimension(300, 50));
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 5;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            if (User.authenticate(idField.getText(), String.valueOf(passwordField.getPassword()))) {
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
