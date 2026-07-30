
import static util.Util.getResourceFileAsString;

import org.json.JSONObject;

import model.Specialty;
import orm.Table;

public class Main {

    public static void main(String[] args) {
        Table.JVMInit(args[0].split("\n"));

        var fileContent = getResourceFileAsString(Main.class, "samples/pedagogical_structure.json");
        var jsonCollection = new JSONObject(fileContent);
        assert jsonCollection.length() == 1 : "The sample data has to be a collection!";

        var key = jsonCollection.keys().next();
        Specialty specialty = (Specialty) Seed.seed(jsonCollection.getJSONArray(key), key, null)[0];
        System.out.println(Seed.last_serialized);
    }
}
