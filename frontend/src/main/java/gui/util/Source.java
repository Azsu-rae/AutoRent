package util;

import util.Listener.Event;

public interface Source {
    void notifyListener(Event event);
}
