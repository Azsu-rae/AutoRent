
import static util.Util.getResourceFileAsString;

import org.json.JSONObject;

import model.Specialty;
import orm.Reflection;
import orm.Table;

public class Main {

    public static void main(String[] args) {
        Table.JVMInit(args[0].split("\n"));
        // Table.debug();

        Reflection.migrateModels();
    }
}
