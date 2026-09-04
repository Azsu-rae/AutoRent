package orm.serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;

import orm.Table;
import orm.reflect.Model;
import orm.reflect.Model.Column;
import util.JSONSchemaException;

import static util.Log.*;

/**
 * Seed
 */
public class Serializable {

    private static List<Serializable> jsonArrayToSerializableCollection(JSONArray array, Model<?> model) {
        return IntStream
            .range(0, array.length())
            .mapToObj(array::getJSONObject)
            .map(o -> new Serializable(model, o))
            .toList();
    }

    private JSONObject json;
    private Table<?> tuple;
    private Model<?> model;

    private List<Serializable> aggregatedCollection = null;

    public Serializable(Model<?> model, JSONObject json) {
        this.json = json;
        this.model = model;
    }

    public Serializable(Table<?> tuple, JSONArray aggregatedCollection, Model<?> aggregatedModel) {
        this.tuple = tuple;
        this.model = tuple.model;
        this.aggregatedCollection = jsonArrayToSerializableCollection(aggregatedCollection, aggregatedModel);
    }

    public static int countLeafs(Serializable serializable) {
        if (serializable.aggregatedCollection != null) {
            int count = 0;
            for (var agg : serializable.aggregatedCollection) {
                count += countLeafs(agg);
            }
            return count;
        } else {
            return 1;
        }
    }

    public Table<?> serialize(Table<?> aggregator) {

        if (tuple != null) {
            return null;
        }

        tuple = model.createInstance();
        for (String key : json.keySet()) {

            if (Model.ofCollectionName(key) != null) {
                var aggregatedModel = Model.ofCollectionName(key);
                aggregatedCollection = jsonArrayToSerializableCollection(json.getJSONArray(key), aggregatedModel);
                continue;
            }

            if (!model.fields.contains(key)) {
                notice("Ignoring field %s while serializing", key);
                continue;
            }

            var curr = json.get(key);
            if (curr instanceof JSONArray) {
                throw new JSONSchemaException("Can't have jsonArrays as a model field!");
            } else if (curr instanceof JSONObject seed) {
                curr = new Serializable(Model.ofWildcard(model.fields.byName(key).getType()), seed).serialize((Table<?>) null);
            }

            tuple.reflect(model.fields.byName(key)).setValue(curr);
        }

        if (aggregator != null) {
            List<Column> ofAggregatorType = model.fields.ofType(aggregator.getClass());
            if (ofAggregatorType.size() == 1) {
                tuple.reflect(ofAggregatorType.get(0).field).setValue(aggregator);
            } else if (ofAggregatorType.size() == 0) {
                String s = "No aggregator-compatibe field found in %s for the aggregator type: %s";
                throw new JSONSchemaException(s, model, aggregator.getClass());
            } else {
                String s ="Ambigious aggregator! Found %d fields of the same type!";
                throw new JSONSchemaException(s, ofAggregatorType.size());
            }
        }

        // executing the deffered serialization
        serializeAggregated();

        return tuple;
    }

    public void serializeAggregated() {

        if (tuple == null) {
            throw new IllegalStateException("You cannot serialized aggregates before the aggregator!");
        }

        if (aggregatedCollection != null) {
            for (var serializable : aggregatedCollection) {
                serializable.serialize(tuple);
            }
        }
    }

    public int persist() {
        if (tuple == null) {
            return 0;
        }

        if (aggregatedCollection != null) {
            int count = 0;
            for (var aggregated : aggregatedCollection) {
                count += aggregated.persist();
            }
            return count;
        } else {
            return tuple.add();
        }
    }
}
