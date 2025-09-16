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

        MyPanel menu = new MyPanel();
        menu.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // ID Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        menu.add(new MyLabel("Username or Email"), gbc);

        // Email Field
        JTextField idField = field(15, Field.TEXT);
        idField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 0;
        gbc.gridy = 1;
        menu.add(idField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        menu.add(new MyLabel("Password"), gbc);

        // Password Field
        JPasswordField passwordField = (JPasswordField) field(15, Field.PASSWORD);
        passwordField.setPreferredSize(new Dimension(300, 40));
        gbc.gridx = 0;
        gbc.gridy = 4;
        menu.add(passwordField, gbc);

        // Login Button
        MyButton loginBtn = new MyButton("Sign in");
        loginBtn.setPreferredSize(new Dimension(300, 60));
        gbc.gridx = 0;
        gbc.gridy = 5;
        menu.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            if (User.authenticate(idField.getText(), String.valueOf(passwordField.getPassword()))) {
                notifyListener(listener);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login!");
            }
        });

        MyPanel wrapper = new MyPanel();
        wrapper.add(menu);

        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(wrapper, gbc);
    }

    @Override
    public void notifyListener(Listener listener) {
        listener.onEvent(Event.LOG_IN);
    }
}
