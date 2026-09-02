
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mannara.Api;
import model.AcademicLevel;
import model.Group;
import orm.Table;
import orm.reflect.Model;

/**
 *
 * Test
 */
public class Test {

    static void init() {

        Model.migrateAll();

        new Api.Seed(Api.Seed.fetchCollection("specialties")).persist();
        // new Api.Seed(Api.Seed.fetchCollection("academicLevels")).persist();
    }

    static void print(Object[] objects) {
        IO.println("[" + Stream.of(objects).map(o -> o.toString()).collect(Collectors.joining(", ")) + "]");
    }
}
