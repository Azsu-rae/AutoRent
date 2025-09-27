package gui.dashboard;

import javax.swing.*;
import java.awt.*;

import gui.component.MyButton;
import gui.component.MyPanel;
import gui.util.Listener;
import gui.util.Listener.Event;
import gui.util.Source;

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
        setUpButton("Records", Event.MODELS);
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
