package aex;

import static util.Util.getResourceFileAsString;

import org.json.JSONObject;

import model.Specialty;
import orm.Reflection;
import orm.Table;
import util.Util;

public class Main {

    public static void main(String[] args) {
        // must always run. DO NOT REMOVE
        Table.JVMInit(args[0].split("\n"));

        var pedagogical_structure_str = Util.getResourceFileAsString(Seed.class, "sample_collections/specialties.json");
        System.out.println(pedagogical_structure_str);
        // new JSONObject(pedagogical_structure_str).getJSONArray("specialties");
    }
}
