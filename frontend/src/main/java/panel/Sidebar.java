package panel;

import java.awt.Color;
import java.awt.Dimension;
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

        setUpButton("Home", Integer.MAX_VALUE, 40, Event.HOME);
        setUpButton("Vehicles", Integer.MAX_VALUE, 40, Event.VEHICLES);
        setUpButton("Clients", Integer.MAX_VALUE, 40, Event.CLIENTS);
        setUpButton("Reservations", Integer.MAX_VALUE, 40, Event.RESERVATIONS);
        setUpButton("Sign out", Integer.MAX_VALUE, 40, Event.LOG_OUT);

        setOpaque(false);
    }

    private void setUpButton(String name, int width, int height, Event event) {

        MyButton btn = new MyButton(name);
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
