package gui.util;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        DISCRETE, BOUNDED,
    }

    void onEvent(Event e);
}
