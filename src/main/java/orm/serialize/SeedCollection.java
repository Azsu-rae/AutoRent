package orm.serialize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.IdentityHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;
import orm.reflect.Model;
import orm.reflect.Model.Column;
import util.JSONSchemaException;
import static util.Log.*;

/**
 * SeedCollection
 *
 * serializes seeding data with a tree-like structure into a tree data structure
 */
public class SeedCollection {

    private Model<?> model;
    private JSONArray collection;
    private Table<?> aggregator;

    private List<Table<?>> serialized;
    private List<SeedCollection> aggregatedCollections = new ArrayList<SeedCollection>();

    public SeedCollection(Model<?> model, JSONArray collection, Table<?> aggregator) {
        this.model = model;
        this.collection = collection;
        this.aggregator = aggregator;
    }

    public boolean isSerailzed() {
        return serialized == null;
    }

    public List<Table<?>> getSerialized() {
        try {
            return Collections.unmodifiableList(serialized);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Unserialized collection!");
        }
    }

    public void serialize() {
        serialized = new ArrayList<Table<?>>();
        for (int i=0;i<collection.length();i++) {
            serialized.add(serialize(collection.getJSONObject(i)));
        }

        for (var aggregatedCollection : aggregatedCollections) {
            aggregatedCollection.serialize();
        }
    }

    private Table<?> serialize(JSONObject seed) {

        var tuple = model.createInstance();
        for (String key : seed.keySet()) {

            if (Model.ofCollectionName(key) != null) {
                aggregatedCollections.add(new SeedCollection(Model.ofCollectionName(key), seed.getJSONArray(key), tuple));
                continue;
            }

            if (!model.fields.contains(key)) {
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

        if (aggregator != null) {
            List<Column> ofAggregatorType = model.fields.ofType(aggregator.getClass());
            if (ofAggregatorType.size() == 1) {
                tuple.reflect(ofAggregatorType.get(0).field).setValue(aggregator);
            } else {
                throw new JSONSchemaException("Ambigious aggregator!");
            }
        }

        return tuple;
    }
}
