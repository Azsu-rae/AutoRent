package util;

import util.Listener.Event;

public interface Filter {

    static public class DiscreteValues {
        final public String name;
        final public Object[] values; 
        public DiscreteValues(String name, Object[] values) {
            this.name = name;
            this.values = values;
        }
    }

    public Object getCriteria(Event event);
}
