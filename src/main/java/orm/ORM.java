package orm;

import java.util.Arrays;

/**
 * ORM
 *
 * This is basically just a singleton to hold the immutable state of the ORM.
 * For now that just means the list of models that the application was launched
 * with.
 *
 * The inialization is also responsible for triggering the loading of those
 * models.
 *
 */
public class ORM {

    final String[] bootModelNames;

    private ORM(String[] modelNames) {
        bootModelNames = Arrays.copyOf(modelNames, modelNames.length);
    }

    static ORM instance;

    public static ORM initializeInstance(String[] modelNames) {
        if (instance != null) {
            throw new IllegalStateException("ORM Singleton's instance has already been intialized");
        }

        instance = new ORM(modelNames);
        Model.touch();

        return instance;
    }

    public static ORM getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ORM Singleton has not been intialized!");
        }
        return instance;
    }
}
