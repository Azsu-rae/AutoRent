package util;

public interface Listener {

    public enum Event {
        LOG_IN,
        LOG_OUT
    }

    void onEvent(Event e);
}
