package mannara;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;
import orm.reflect.Model;
import orm.serialize.SeedCollection;

import util.BugDetectedException;
import util.JSONSchemaException;

/**
 * API ASSUMPTIONS
 *
 * - when a collection is aggregated the aggregator uses the camelCase of the model name
 *   as the field label
 * - collections are aggregated but individual seeds are directly referenced
 * - the field names and types must perfectly corresponds without overflow
 * - this is overall just a very fragile and unforgiving sampling method but it's a first
 *   implementatoin
 *
 */

/**
 * Mannara
 *
 */
public class Api {

    private static final String URL = "http://127.0.0.1:8000";

    public static class Seed {

        private static final String PATH = "/seed/collection/";

        static public JSONObject fetchCollection(String collectionName) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL + PATH + collectionName))
                    .build();
            return HttpClient.newHttpClient().sendAsync(httpRequest, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(JSONObject::new)
                    .join();
        }

        List<SeedCollection> collections = new ArrayList<>();
        Model<?> model;

        public Seed(JSONObject payload) {

            var meta = payload.getJSONObject("meta");
            var data = payload.get("data");

            model = Model.ofCollectionName(meta.getString("collectionName"));
            if (model == null) {
                throw new JSONSchemaException("Unknown collection name `%s`", meta.getString("collectionName"));
            }

            if (meta.getBoolean("aggregated")) {

                var aggregatedBy = meta.getJSONObject("aggregatedBy");

                var aggregatorModel = Model.ofName(aggregatedBy.getString("model"));
                if (aggregatorModel == null) {
                    throw new JSONSchemaException("Unknown model name `%s`", aggregatedBy.getString("model"));
                }

                var aggregatorField = aggregatorModel.fields.byName(aggregatedBy.getString("field"));
                if (aggregatorField == null) {
                    throw new JSONSchemaException("Unknown field name `%s`", aggregatedBy.getString("field"));
                }

                for (var aggregatorClue : ((JSONObject) data).keySet()) {
                    var aggregator = fetchAggregator(aggregatorModel, aggregatorField, aggregatorClue);
                    collections.add(new SeedCollection(model, ((JSONObject) data).getJSONArray(aggregatorClue), aggregator));
                }
            } else {
                collections.add(new SeedCollection(model, (JSONArray) data, null));
            }
        }

        private Table<?> fetchAggregator(Model<?> model, Field field, String clue) {
            var queryResult = Table.search(model, field, clue);
            if (queryResult.size() != 1) {
                throw new BugDetectedException("Aggregator clue isn't a candidate key");
            }
            return queryResult.get(0);
        }

        public void persist() {
            for (var collection : collections) {
                collection.serialize();
            }
        }
    }
}
