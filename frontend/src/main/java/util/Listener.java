package util;

public interface Listener {

    public enum Event {
        LOG_IN, LOG_OUT,
        HOME, VEHICLES, CLIENTS, RESERVATIONS,
    }

    void onEvent(Event e);
}
