package gui.dashboard.record;

import javax.swing.*;

import java.util.*;

import gui.dashboard.record.dialog.SearchProfile;
import gui.action.FilterAction;
import gui.component.Factory;
import gui.component.MyButton;

import gui.contract.Listener;

import orm.Table.Range;
import orm.util.Constraints;
import orm.Table;

import static orm.Reflection.getModelInstance;
import static orm.Reflection.fieldsOf;

public class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    Vector<Range> boundedValues = new Vector<>();
    FilterAction action;
    String ORMModelName;

    public ToolBar(String ORMModelName, FilterAction action) {

        var fields = fieldsOf(ORMModelName);
        this.ORMModelName = ORMModelName;
        this.action = action;

        var searchedTexts = fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(Factory.createSearchBar(attributes -> {
                for (var name : searchedTexts.toArray(String[]::new)) {
                    discreteValues.computeIfAbsent(name, k -> new ArrayList<>()).add(attributes[0].value());
                } onApply();
            }));
        }

        var unique = fields.haveConstraint(Constraints::unique);
        if (unique.size() > 0) {
            add(new MyButton("Search Profile", e -> new SearchProfile(unique.toArray(String[]::new), attributes -> {
                for (var att : attributes) {
                    discreteValues.computeIfAbsent(att.name(), k -> new ArrayList<>()).add(att.value());
                } onApply();
            }).showDialog()));
        }

//        for (var enumerated : model.reflect.fields.haveConstraint(Constraints::enumerated)) {
//            var title =  model.parser.formatName(enumerated);
//            add(new MyButton(this, title, e -> new MultipleSelections(this, title, enumerated)));
//        }
//
//        for (var bounded : model.reflect.fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
//            var title =  model.parser.formatName(bounded);
//            add(new MyButton(this, title, e -> new RangeSelection(this, title, bounded)));
//        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Reset", e -> action.onFilter(null)));
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

        action.onFilter(Table.search(discreteCriterias, boundedValues));

        discreteValues = new HashMap<>();
        boundedValues = new Vector<>();
    }
}
