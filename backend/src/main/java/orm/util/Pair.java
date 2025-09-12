package orm.util;

abstract public class Pair<U,V> {

    protected U first;
    protected V second;

    protected Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s]", first, second);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) 
            return true;

        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Pair<?,?> pair = (Pair<?,?>) obj;
        return (first != null ?  first.equals(pair.first) : first == pair.first)
            && (second != null ?  second.equals(pair.second) : second == pair.second);
    }

    @Override
    public int hashCode() {
        int result = 31 * (first != null ? first.hashCode() : 0);
        result += 31 * (second != null ? second.hashCode() : 0);
        return result;
    }
}
