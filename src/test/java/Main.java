
import orm.ORM;

public class Main {
    public static void main(String[] args) {
        // args (containing the model names) must always be forwared
        ORM.initializeInstance(args);
    }
}
