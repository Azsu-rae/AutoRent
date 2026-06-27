package gui.dashboard.record;

import javax.swing.*;

import java.util.*;
import java.util.function.Consumer;

import gui.dashboard.record.dialog.RangeSelection;
import gui.dashboard.record.dialog.SearchProfile;
import mapper.FieldLabelFormatter;
import mapper.FieldValueMapper;
import gui.dashboard.record.dialog.ForeignPicker;
import gui.dashboard.record.dialog.MultipleSelections;

import component.Factory;
import component.MyButton;
import component.MyPanel;

import orm.Table.Range;
import orm.Constraints;
import orm.Table;

import static orm.Reflection.getModelInstance;
import static orm.Reflection.fieldsOf;

import static mapper.FieldLabelFormatter.titleCase;

class ToolBar extends JToolBar {

    private Map<String, List<Object>> discreteValues = new HashMap<>();
    private Vector<Range> boundedValues = new Vector<>();

    private void addDiscreteValues(String name, Object... values) {
        for (var val : values) {
            discreteValues
                    .computeIfAbsent(name, k -> new ArrayList<>())
                    .add(val);
        }

    }

    private Consumer<Vector<Table>> filterAction;
    private String ORMModelName;

    private FieldLabelFormatter fieldLabelFormatter;
    private FieldValueMapper fieldValueMapper;

    public ToolBar(String ORMModelName, Consumer<Vector<Table>> filterAction) {

        this.filterAction = filterAction;
        this.ORMModelName = ORMModelName;

        fieldLabelFormatter = new FieldLabelFormatter(ORMModelName);
        fieldValueMapper = new FieldValueMapper(ORMModelName);

        var fields = fieldsOf(ORMModelName);

        var searchedTexts = fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(searchBar(searchedTexts));
        }

        var uniques = fields.haveConstraint(Constraints::unique);
        if (uniques.size() > 0) {
            add(new MyButton("Search Profile", e -> searchProfile(uniques)));
        }

        for (var enumerated : fields.haveConstraint(Constraints::enumerated)) {
            var title = fieldLabelFormatter.formatAttNameForFiltering(enumerated);
            add(new MyButton(title, e -> multipleSelections(title, enumerated)));
        }

        for (var bounded : fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var title = fieldLabelFormatter.formatAttNameForFiltering(bounded);
            add(new MyButton(title, e -> rangeSelection(title, bounded)));
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
            for (int i = 0; i < discrete.getValue().size(); i++) {
                Object value = discrete.getValue().get(i);
                if (i >= discreteCriterias.size()) {
                    discreteCriterias.add(getModelInstance(ORMModelName));
                }
                discreteCriterias.elementAt(i).reflect.fields.set(discrete.getKey(), value);
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
            }
            onApply();
        });
    }

    private void searchProfile(List<String> uniques) {
        new SearchProfile(uniques.toArray(String[]::new), attributes -> {
            for (var attribute : attributes.entrySet()) {
                addDiscreteValues(attribute.getKey(), attribute.getValue());
            }
            onApply();
        }).display();
    }

    private void rangeSelection(String title, String attribute) {
        new RangeSelection(
                title,
                fieldLabelFormatter.new RangeLabel(attribute),
                fieldValueMapper.new RangeParser(attribute),
                range -> boundedValues.add(range)).display();
    }

    private void multipleSelections(String title, String attribute) {
        var choices = getModelInstance(ORMModelName).getEnumeratedValuesOf(attribute).toArray(String[]::new);
        new MultipleSelections(title, choices, selectedChoices -> {
            addDiscreteValues(attribute, (Object[]) selectedChoices);
        }).display();
    }

    private void foreignPicker(String title) {
        new ForeignPicker(title, ORMModelName, tuple -> {
            addDiscreteValues(tuple.getClass().getSimpleName().toLowerCase(), tuple);
        }).display();
    }
}
