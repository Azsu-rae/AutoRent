package gui.contract;

public interface Listener {

    public enum Event {
        HOME, MODELS,
        LOG_IN, LOG_OUT,
        CLEAR, SELECTION,
        LOAD
    }

    void onEvent(Event e);
}
