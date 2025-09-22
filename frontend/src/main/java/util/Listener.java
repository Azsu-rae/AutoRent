package util;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        DISCRETE, BOUNDED,
        VEHICLES, CLIENTS, RESERVATIONS, USERS
    }

    void onEvent(Event e);
}
