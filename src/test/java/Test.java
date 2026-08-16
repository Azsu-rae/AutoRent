
import mannara.Api;
import orm.Reflection;

/**
 *
 * Test
 */
public class Test {

    static void init() {

        Reflection.migrateModels();

        new Api.JSONSeed(Api.Seed.fetchCollection("specialties")).persist();
        new Api.JSONSeed(Api.Seed.fetchCollection("academicLevels")).persist();
    }
}
