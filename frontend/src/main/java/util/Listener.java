package util;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        VEHICLES, CLIENTS, RESERVATIONS, USERS
    }

    void onEvent(Event e);
}
