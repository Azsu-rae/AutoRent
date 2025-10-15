package gui.dashboard.record;

import javax.swing.*;

import java.util.*;
import java.util.function.Consumer;

import gui.dashboard.record.dialog.RangeSelection;
import gui.dashboard.record.dialog.SearchProfile;
import gui.dashboard.record.dialog.ForeignPicker;
import gui.dashboard.record.dialog.MultipleSelections;
import gui.util.Attribute;
import gui.component.Factory;
import gui.component.MyButton;
import gui.component.MyPanel;

import orm.Table.Range;
import orm.util.Constraints;
import orm.Table;
import orm.Reflection.FieldInfos;

import static orm.Reflection.getModelInstance;
import static orm.Reflection.fieldsOf;

import static gui.util.Parser.formatName;
import static gui.util.Parser.titleCase;
import static gui.util.Parser.titleCaseNames;

public class ToolBar extends JToolBar {

    private Map<String,List<Attribute<?>>> discreteValues = new HashMap<>();
    private <T> void addDiscreteValues(String name, T value) {
        discreteValues
            .computeIfAbsent(name, k -> new ArrayList<>())
            .add(new Attribute<T>(name).addValue(value));
    }

    private Vector<Range> boundedValues = new Vector<>();
    private Consumer<Vector<Table>> filterAction;
    private String ORMModelName;

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
            add(new MyButton(title, e -> multipleSelections(title, attribute)));
        }

        for (var bounded : fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var attribute = new Attribute<Object>(ORMModelName, bounded);
            var title =  formatName(attribute);
            add(new MyButton(title, e -> rangeSelection(title, attribute, fields)));
        }

        for (var foreign : fields.haveConstraint(Constraints::foreignKey)) {
            String title = String.format("Select a %s", titleCase(foreign));
            add(new MyButton(foreign, e -> foreignPicker(title)));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Reset", e -> filterAction.accept(null)));
        add(new MyButton("Apply", e -> onApply()));
    }

    private void onApply() {

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

    private MyPanel searchBar(List<String> searchedTexts) {
        return Factory.createSearchBar(attributeValue -> {
            for (var name : searchedTexts) {
                addDiscreteValues(name, attributeValue);
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
            for (var value : attributeValues.getValues()) {
                addDiscreteValues(attributeValues.name, value);
            } return true;
        }).display();
    }

    private void foreignPicker(String title) {
        new ForeignPicker(title, ORMModelName, tuple -> {
            addDiscreteValues(tuple.getClass().getSimpleName().toLowerCase(), tuple);
            return true;
        }).display();
    }
}
