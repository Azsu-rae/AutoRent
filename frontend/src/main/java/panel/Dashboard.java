package panel;

import java.awt.BorderLayout;

import ui.component.*;

import util.Listener;
import util.Source;

import panel.Sidebar;

import util.Listener.Event;

public class Dashboard extends MyPanel implements Source, Listener {

    public Dashboard(Listener listener) {
        setLayout(new BorderLayout());
        add(new Sidebar(this), BorderLayout.WEST);
    }

    @Override
    public void notifyListener(Listener listener, Event event) {

    }

    @Override
    public void onEvent(Event event) {}
}
