
import mannara.Api;
import orm.reflect.Reflection;

/**
 *
 * Test
 */
public class Test {

    static void init() {

        Reflection.migrateModels();

        new Api.Seed(Api.Seed.fetchCollection("specialties")).persist();
        new Api.Seed(Api.Seed.fetchCollection("academicLevels")).persist();
    }
}
