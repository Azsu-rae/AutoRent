package gui.dashboard.record;

import javax.swing.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gui.dashboard.record.dialog.RangeSelection;
import gui.dashboard.record.dialog.SearchProfile;
import gui.dashboard.record.dialog.MultipleSelections;
import gui.util.Attribute;
import gui.util.Parser;
import gui.component.Factory;
import gui.component.MyButton;
import gui.component.MyPanel;
import gui.contract.Listener;

import orm.Table.Range;
import orm.util.Console;
import orm.util.Constraints;
import orm.Table;
import orm.Reflection.FieldInfos;

import static orm.Reflection.getModelInstance;
import static orm.Reflection.fieldsOf;

import static gui.util.Parser.formatName;

public class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    void addDiscreteValues(String name, String value) {
        discreteValues
            .computeIfAbsent(name, k -> new ArrayList<>())
            .add(value);
    }
    Vector<Range> boundedValues = new Vector<>();
    Consumer<Vector<Table>> filterAction;
    String ORMModelName;

    public ToolBar(String ORMModelName, Consumer<Vector<Table>> filterAction) {

        var fields = fieldsOf(ORMModelName);
        this.ORMModelName = ORMModelName;
        this.filterAction = filterAction;

        var searchedTexts = fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(searchBar(searchedTexts));
        }

        var uniques = fields.haveConstraint(Constraints::unique);
        if (uniques.size() > 0) {
            add(new MyButton("Search Profile", e -> searchProfile(uniques)));
        }

        for (var enumerated : fields.haveConstraint(Constraints::enumerated)) {
            var attribute = new Attribute<String>(ORMModelName, enumerated);
            var title =  formatName(attribute);
            add(new MyButton(this, title, e -> multipleSelections(title, attribute)));
        }

        for (var bounded : fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var attribute = new Attribute<Object>(ORMModelName, bounded);
            var title =  formatName(attribute);
            add(new MyButton(this, title, e -> rangeSelection(title, attribute, fields)));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Reset", e -> filterAction.accept(null)));
        add(new MyButton("Apply", e -> onApply()));
    }

    private MyPanel searchBar(List<String> searchedTexts) {
        return Factory.createSearchBar(attribute -> {
            for (var name : searchedTexts) {
                addDiscreteValues(name, attribute);
            } onApply();
        });
    }

    private void searchProfile(List<String> uniques) {
        new SearchProfile(uniques.toArray(String[]::new), attributes -> {
            for (var attribute : attributes) {
                addDiscreteValues(attribute.name, attribute.getSingleValue());
            } onApply();
            return true;
        }).display();
    }

    private void rangeSelection(String title, Attribute<Object> attribute, FieldInfos fields) {
        new RangeSelection(title, attribute, range -> {
            if (!range.isValidCriteriaFor(fields)) {
                return false;
            } boundedValues.add(range);
            return true;
        }).display();
    }

    private void multipleSelections(String title, Attribute<String> attribute) {
        new MultipleSelections(title, attribute, attributeValues -> {
            for (var value : attributeValues.values) {
                addDiscreteValues(attributeValues.name, value);
            } return true;
        }).display();
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
