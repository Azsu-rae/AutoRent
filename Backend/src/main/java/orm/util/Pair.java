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

        return String.format("%s = [%s, %s]", attributeName, first, second);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) 
            return true;

        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Pair<?,?> pair = (Pair<?,?>) obj;
        return
            (attributeName != null ?  attributeName.equals(pair.attributeName) : attributeName == pair.attributeName)
            && (first != null ?  first.equals(pair.first) : first == pair.first)
            && (second != null ?  second.equals(pair.second) : second == pair.second);
    }

    @Override
    public int hashCode() {

        int result = attributeName != null ? attributeName.hashCode() : 0;
        result += 31 * (first != null ? first.hashCode() : 0);
        result += 31 * (second != null ? second.hashCode() : 0);

        return result;
    }

    public boolean isValidCriteriaFor(Reflection r) {

        return
            attributeName != null && first != null && second != null
            && first.getClass().equals(second.getClass())
            && r.getBoundedFieldNames().contains(attributeName)
            && r.getFieldClass(attributeName).equals(first.getClass());
    }
}
