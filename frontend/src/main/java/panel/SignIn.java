package panel;

import java.awt.*;
import javax.swing.*;

import ui.component.*;

import util.*;
import util.Listener.Event;

import orm.model.User;

import static ui.Factory.*;

public class SignIn extends MyPanel implements Source {

    Listener listener;

    public SignIn(Listener listener) {
        this.listener = listener;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID Label
        gbc.gridx = 0; gbc.gridy = 0;
        add(new MyLabel("Username or Email"), gbc);

        // ID Field
        JTextField idField = field(15, Field.TEXT, 300, 40);
        gbc.gridx = 0; gbc.gridy = 1;
        add(idField, gbc);

        // Password Label
        gbc.gridx = 0; gbc.gridy = 3;
        add(new MyLabel("Password"), gbc);

        // Password Field
        JPasswordField passwordField = (JPasswordField) field(15, Field.PASSWORD, 300, 40);
        gbc.insets = new Insets(5, 5, 15, 5);
        gbc.gridx = 0; gbc.gridy = 4;
        add(passwordField, gbc);

        // Login Button
        MyButton loginBtn = new MyButton("Sign in", 300, 50);
        gbc.insets = new Insets(15, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 5;
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            if (User.authenticate(idField.getText(), String.valueOf(passwordField.getPassword()))) {
                idField.setText(""); passwordField.setText("");
                notifyListener(Event.LOG_IN);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login!");
            }
        });
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }
}
