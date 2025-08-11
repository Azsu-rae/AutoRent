package orm.util;

public class Pair<U, V> {

    public String attributeName;

    public U first;
    public V second;

    public Pair() {}

    public Pair(U first, V second) {

        this.first = first;
        this.second = second;
    }

    public Pair(String attributeName, U first, V second) {

        this.attributeName = attributeName;
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {

        return attributeName + " = [" + first.toString() + ", " + second.toString() + "]";
    }

    public boolean isValid() {

        return attributeName != null && first != null && second != null && first.getClass().equals(second.getClass());
    }
}
