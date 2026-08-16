package mannara;

import static orm.Reflection.getModelInstance;
import static util.CaseConverter.pascalToCamel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;

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

    public static Table last_serialized = null;

    public static void debug(JSONSeed jsonSeed) {
        jsonSeed.fetchAggregator("ST");
    }

    public static class JSONSeed {

        String name;
        boolean aggregated;
        JSONObject aggregatedBy = null;

        Object data;

        public JSONSeed(JSONObject seedCollection) {

            var meta = seedCollection.getJSONObject("meta");
            this.name = meta.getString("name");
            this.aggregated = meta.getBoolean("aggregated");
            if (aggregated) {
                this.aggregatedBy = meta.getJSONObject("aggregatedBy");
            }

            this.data = seedCollection.get("data");
        }

        @Override
        public String toString() {
            return name
                    + "is "
                    + (aggregated ? "aggregated" : "not aggregated")
                    + "and the type of aggregatedBy is "
                    + (aggregatedBy != null ? aggregatedBy.getClass().toString() : "null");
        }

        private Table fetchAggregator(String clue) {
            String modelName = aggregatedBy.getString("model");
            String fieldName = aggregatedBy.getString("field");

            Table criteria = getModelInstance(modelName).reflect.fields.set(fieldName, clue);
            Vector<Table> queryResult = Table.search(criteria);
            assert queryResult.size() == 1 : "Aggregator clue isn't a candidate key";

            return queryResult.get(0);
        }

        public void persist() {
            Vector<Table> serializedSeeds = new Vector<>();
            if (aggregated) {
                var seedCollections = ((JSONObject) data);
                Iterator<String> keys = seedCollections.keys();
                do {
                    var aggregatorClue = keys.next();
                    serializedSeeds.addAll(List.of(serializeSeedCollection(
                            seedCollections.getJSONArray(aggregatorClue),
                            name,
                            fetchAggregator(aggregatorClue))));
                } while (keys.hasNext());
            } else {
                serializedSeeds.addAll(List.of(serializeSeedCollection((JSONArray) data, name)));
            }
        }

        private static Table[] serializeSeedCollection(JSONArray collection, String collectionName) {
            return serializeSeedCollection(collection, collectionName, null);
        }

        private static Table[] serializeSeedCollection(
                JSONArray seedCollection,
                String collectionName,
                Table aggregator) {

            var serializedSeeds = new Table[seedCollection.length()];
            String modelName = Table.getModelNameFromCollectionName(collectionName);
            for (int i = 0; i < seedCollection.length(); i++) {
                JSONObject seed = seedCollection.getJSONObject(i);
                serializedSeeds[i] = serializeSeed(modelName, seed, aggregator);
            }

            return serializedSeeds;
        }

        private static Table serializeSeed(String modelName, JSONObject seed, Table aggregator) {

            var collectionKeys = new ArrayList<String>();
            Table serializedSeed = getModelInstance(modelName);
            for (var key : seed.keySet()) {
                Object attributeValue = null;
                if (Table.isKeyACollection(key)) {
                    collectionKeys.add(key);
                } else if (serializedSeed.isFieldOfModel(key)) {
                    attributeValue = serializeSeed(
                            serializedSeed.reflect.fields.typeOf(key).getSimpleName(),
                            seed.getJSONObject(key), null);
                } else {
                    attributeValue = seed.get(key);
                }

                if (attributeValue != null) {
                    serializedSeed.reflect.fields.callSetter(key, attributeValue);
                }
            }

            if (aggregator != null) {
                serializedSeed.reflect.fields.callSetter(
                        pascalToCamel(aggregator.getClass().getSimpleName()),
                        aggregator);
            }

            serializedSeed.add();
            for (var key : collectionKeys) {
                serializeSeedCollection(seed.getJSONArray(key), key, serializedSeed);
            }

            return serializedSeed;
        }
    }

    public static class Seed {

        static public JSONObject fetchCollection(String collectionName) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/seed/collection/" + collectionName))
                    .build();
            return HttpClient.newHttpClient().sendAsync(httpRequest, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(JSONObject::new)
                    .join();
        }
    }
}
