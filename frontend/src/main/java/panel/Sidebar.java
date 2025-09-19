package panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import ui.component.*;

import util.Listener;
import util.Source;

import util.Listener.Event;

public class Sidebar extends MyPanel implements Source {

    GridBagConstraints gbc = new GridBagConstraints();
    Listener listener;

    public Sidebar(Listener listener) {
        this.listener = listener;

        setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = -1;

        setUpButton("Home", Event.HOME);
        setUpButton("Vehicles", Event.VEHICLES);
        setUpButton("Clients", Event.CLIENTS);
        setUpButton("Reservations", Event.RESERVATIONS);
        setUpButton("Sign out", Event.LOG_OUT);
    }

    private void setUpButton(String name, Event event) {
        MyButton btn = new MyButton(name, SwingConstants.LEFT);
        gbc.gridy++; add(btn, gbc);
        btn.addActionListener(e -> {
            notifyListener(event);
        });
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }
}
