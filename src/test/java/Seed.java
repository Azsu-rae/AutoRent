
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import model.*;

import orm.Table;
import util.CaseConverter;

import static util.CaseConverter.*;

import static orm.Reflection.getModelInstance;

public class Seed {

    public static Table last_serialized = null;

    static Table[] seed(JSONArray jsonArray, String collectionName, Table aggregator) {

        var seeded = new Table[jsonArray.length()];
        String modelName = Table.getModelNameFromCollectionName(collectionName);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            seeded[i] = serialize(modelName, jsonObject, aggregator);
        }

        return seeded;
    }

    static Table serialize(String modelName, JSONObject jsonObject, Table aggregator) {

        Table instance = getModelInstance(modelName);
        last_serialized = instance;
        for (var key : jsonObject.keySet()) {
            if (Table.isACollection(key)) {
                seed(jsonObject.getJSONArray(key), key, instance);
            } else if (instance.isFieldAModelInstance(key)) {
                serialize(camelToPascal(key), jsonObject.getJSONObject(key), null);
            } else {
                instance.reflect.fields.callSetter(key, jsonObject.get(key));
            }
        }

        if (aggregator != null) {
            instance.reflect.fields.callSetter(aggregator.getClass(), aggregator);
        }

        return instance;
    }

    static void naive_seeding() {

        InputStream in = Main.class.getResourceAsStream("samples/curriculum_structure.json");
        if (in == null) {
            throw new RuntimeException("Resouce not found!");
        }

        String curriculum_string = "";
        try {
            curriculum_string = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        in = Main.class.getResourceAsStream("samples/pedagogical_structure.json");
        if (in == null) {
            throw new RuntimeException("Resouce not found!");
        }

        String pedagogical_string = "";
        try {
            pedagogical_string = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        var specialty = new Specialty();
        Student last_student = null;

        JSONArray pedagogical_json = new JSONArray(pedagogical_string);
        for (int i = 0; i < pedagogical_json.length(); i++) {
            var specialty_data = pedagogical_json.getJSONObject(i);
            specialty
                    .setName(specialty_data.getString("name"))
                    .setAcronyme(specialty_data.getString("acronyme"))
                    .setCycle(specialty_data.getString("cycle"));

            var academicLevels = specialty_data.getJSONArray("academicLevels");
            for (int j = 0; j < academicLevels.length(); j++) {
                JSONObject academicLevel_data = academicLevels.getJSONObject(j);
                var academicLevel = new AcademicLevel()
                        .setNumber(academicLevel_data.getInt("number"))
                        .setSpecialty(specialty);

                var sections = academicLevel_data.getJSONArray("sections");
                for (int k = 0; k < sections.length(); k++) {
                    JSONObject section_data = sections.getJSONObject(k);
                    var section = new Section()
                            .setNumber(section_data.getInt("number"))
                            .setAcademicLevel(academicLevel);

                    var groups = section_data.getJSONArray("groups");
                    for (int v = 0; v < groups.length(); v++) {
                        var group_data = groups.getJSONObject(v);
                        var ta_data = group_data.getJSONObject("teachingAssistant");
                        var group = new Group()
                                .setNumber(group_data.getInt("number"))
                                .setSection(section)
                                .setTeachingAssistant(new TeachingAssistant()
                                        .setName(ta_data.getString("name"))
                                        .setEmail(ta_data.getString("email"))
                                        .setSurname(ta_data.getString("surname"))
                                        .setPhoneNumber(ta_data.getString("phoneNumber")));

                        var students = group_data.getJSONArray("students");
                        for (int u = 0; u < students.length(); u++) {
                            var student_data = students.getJSONObject(u);
                            var student = new Student()
                                    .setSurname(student_data.getString("surname"))
                                    .setName(student_data.getString("name"))
                                    .setEmail(student_data.getString("email"))
                                    .setMatricule(student_data.getString("matricule"))
                                    .setGroup(group);

                            last_student = student;
                        }
                    }
                }
            }
        }

        System.out.println(last_student);
    }
}
