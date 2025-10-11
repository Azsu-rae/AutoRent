package gui.dashboard.record;

import javax.swing.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gui.dashboard.record.dialog.RangeSelection;
import gui.dashboard.record.dialog.SearchProfile;
import gui.util.Attribute;
import gui.util.Parser;
import gui.component.Factory;
import gui.component.MyButton;

import gui.contract.Listener;

import orm.Table.Range;
import orm.util.Constraints;
import orm.Table;

import static orm.Reflection.getModelInstance;
import static orm.Reflection.fieldsOf;

import static gui.util.Parser.formatName;

public class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    Vector<Range> boundedValues = new Vector<>();
    Consumer<Vector<Table>> filterAction;
    String ORMModelName;

    public ToolBar(String ORMModelName, Consumer<Vector<Table>> filterAction) {

        var fields = fieldsOf(ORMModelName);
        this.ORMModelName = ORMModelName;
        this.filterAction = filterAction;

        var searchedTexts = fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(Factory.createSearchBar(attribute -> {
                for (var name : searchedTexts.toArray(String[]::new)) {
                    discreteValues.computeIfAbsent(name, k -> new ArrayList<>()).add(attribute.values()[0]);
                } onApply();
            }));
        }

        var unique = fields.haveConstraint(Constraints::unique);
        if (unique.size() > 0) {
            add(new MyButton("Search Profile", e -> new SearchProfile(unique.toArray(String[]::new), attributes -> {
                for (var att : attributes) {
                    discreteValues.computeIfAbsent(att.name(), k -> new ArrayList<>()).add(att.values()[0]);
                } onApply();
            }).display()));
        }

        for (var enumerated : fields.haveConstraint(Constraints::enumerated)) {
            var attribute = new Attribute(ORMModelName, enumerated);
            var title =  formatName(attribute);
//            add(new MyButton(this, title, e -> new MultipleSelections(attribute, )));
        }

        for (var bounded : fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var attribute = new Attribute(ORMModelName, bounded);
            var title =  formatName(new Attribute(ORMModelName, bounded));
            add(new MyButton(this, title, e -> new RangeSelection(title, attribute, (lower, upper) -> {
                var lowerVal = Parser.parse(new Attribute(lower).setModel(ORMModelName));
                var upperVal = Parser.parse(new Attribute(upper).setModel(ORMModelName));
                var range = new Range(bounded, lowerVal, upperVal);
                if (range.isValidCriteriaFor(fields)) {
                    boundedValues.add(range);
                } else {
                    return false;
                } return true;
            }).display()));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Reset", e -> filterAction.accept(null)));
        add(new MyButton("Apply", e -> onApply()));
    }

    void onApply() {

        var discreteCriterias = new Vector<Table>();
        for (var discrete : discreteValues.entrySet()) {
            for (int i=0;i<discrete.getValue().size();i++) {

                Object value = discrete.getValue().get(i);
                if (value == null || value.equals("")) {
                    continue;
                }

                if (i >= discreteCriterias.size()) {
                    discreteCriterias.add(getModelInstance(ORMModelName));
                } discreteCriterias.elementAt(i).reflect.fields.set(discrete.getKey(), value);
            }
        }

        if (discreteCriterias.size() == 0) {
            discreteCriterias.add(getModelInstance(ORMModelName));
        }

        filterAction.accept(Table.search(discreteCriterias, boundedValues));

        discreteValues = new HashMap<>();
        boundedValues = new Vector<>();
    }
}
