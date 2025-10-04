package gui.dashboard.record.toolbar;


import javax.swing.*;

import java.util.*;

import gui.component.MyButton;
import gui.dashboard.record.Record;

import orm.Table.Range;
import orm.util.Constraints;
import orm.Table;

import static orm.util.Reflection.getModelInstance;

public class ToolBar extends JToolBar {

    Vector<Range> boundedValues = new Vector<>();
    Map<String,List<String>> discreteValues = new HashMap<>();
    void addDiscreteCriteria(String att, String value) {
        discreteValues.computeIfAbsent(att, k -> new ArrayList<>()).add(value);
    }

    Record model;
    public ToolBar(Record model) {
        this.model = model;

        var searchedTexts = model.reflect.fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(new SearchBar(this, searchedTexts.toArray(String[]::new)));
        }

        var unique = model.reflect.fields.haveConstraint(Constraints::unique);
        if (unique.size() > 0) {
            add(new MyButton("Search Profile", e -> new SearchProfile(this, unique.toArray(String[]::new))));
        }

        for (var enumerated : model.reflect.fields.haveConstraint(Constraints::enumerated)) {
            var title =  model.parser.formatName(enumerated);
            add(new MyButton(this, title, e -> new MultipleSelections(this, title, enumerated)));
        }

        for (var bounded : model.reflect.fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var title =  model.parser.formatName(bounded);
            add(new MyButton(this, title, e -> new RangeSelection(this, title, bounded)));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Reset", e -> model.recordGrid.grid.loadData()));
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
                    discreteCriterias.add(getModelInstance(model.ORMModelName));
                } discreteCriterias.elementAt(i).reflect.fields.set(discrete.getKey(), value);
            }
        }

        if (discreteCriterias.size() == 0) {
            discreteCriterias.add(getModelInstance(model.ORMModelName));
        }

        model.recordGrid.grid.loadData(Table.search(discreteCriterias, boundedValues));

        discreteValues = new HashMap<>();
        boundedValues = new Vector<>();
    }
}
