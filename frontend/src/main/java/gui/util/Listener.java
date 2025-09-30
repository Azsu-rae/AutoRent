package gui.util;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        DISCRETE, BOUNDED,
        CLEAR, SELECTION
    }

    void onEvent(Event e);
}
