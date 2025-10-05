package gui.util;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        CLEAR, SELECTION
    }

    void onEvent(Event e);
}
