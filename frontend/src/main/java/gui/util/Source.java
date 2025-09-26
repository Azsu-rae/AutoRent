package gui.util;

import gui.util.Listener.Event;

public interface Source {
    void notifyListener(Event event);
}
