package panel;

import java.awt.BorderLayout;

import ui.component.*;

import util.Listener;
import util.Source;

import panel.Sidebar;

import util.Listener.Event;

public class Dashboard extends MyPanel implements Source, Listener {

    Listener listener;

    public Dashboard(Listener listener) {
        this.listener = listener;
        setLayout(new BorderLayout());
        add(new Sidebar(this), BorderLayout.WEST);
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }

    @Override
    public void onEvent(Event event) {

        switch (event) {
            case LOG_OUT:
                notifyListener(event);
                break;
            default:
                break;
        }
    }
}
