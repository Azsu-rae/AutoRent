
import static util.CaseConverter.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import model.AcademicLevel;
import model.Group;
import model.Section;
import model.Specialty;
import model.Student;
import model.TeachingAssistant;
import orm.Reflection;
import orm.Table;
import orm.annotation.Collection;
import util.BugDetectedException;

import static util.Util.*;

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
