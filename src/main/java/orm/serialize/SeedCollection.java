package orm.serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.IdentityHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;
import orm.reflect.Model;

import util.JSONSchemaException;
import static util.Log.*;

/**
 * SeedCollection
 *
 * serializes seeding data with a tree-like structure into a tree data structure
 */
public class SeedCollection {

    Model<?> model;
    JSONArray collection;

    List<Table<?>> serialized;
    List<SeedCollection> aggregatedCollections = new ArrayList<SeedCollection>();

    public SeedCollection(Model<?> model, JSONArray collection, Table<?> aggregator) {
        this.model = model;
        this.collection = collection;
    }

    public void serialize() {
        serialized = new ArrayList<Table<?>>();
        for (int i=0;i<collection.length();i++) {
            serialized.add(serialize(collection.getJSONObject(i)));
        }
    }

    private Table<?> serialize(JSONObject seed) {

        var tuple = model.createInstance();
        for (String key : seed.keySet()) {

            if (Model.byCollectionName(key) != null) {
                aggregatedCollections.add(new SeedCollection(Model.byCollectionName(key), seed.getJSONArray(key), tuple));
                continue;
            }

            if (!model.fields.has(key)) {
                notice("Ignoring field %s while serializing", key);
                continue;
            }

            var curr = seed.get(key);
            if (curr instanceof JSONArray) {
                throw new JSONSchemaException("Can't have jsonArrays as a model field!");
            } else if (curr instanceof JSONObject) {
                curr = serialize((JSONObject) curr);
            }

            tuple.reflect(model.fields.byName(key)).setValue(curr);
        }

        return tuple;
    }
}
