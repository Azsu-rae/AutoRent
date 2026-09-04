
import mannara.Api;
import orm.reflect.Model;

import static util.Log.*;

/**
 *
 * Test
 */
public class Test {

    static void init() {

        Model.migrateAll();

        var seed = new Api.Seed("specialties");
        for (var s : seed.collection) {
            s.persist();
        }

        int count = 0;
        seed = new Api.Seed("academicLevels");
        for (var s : seed.collection) {
            count += s.persist();
        }
        print("persisted = %d", count);
    }
}
