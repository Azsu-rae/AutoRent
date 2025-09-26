package gui.dashboard.records.model.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JToolBar;

import gui.component.MyButton;
import gui.dashboard.records.model.Model;
import orm.Table.Range;
import orm.util.Constraints;

public class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    Map<String,List<Range>> boundedValues = new HashMap<>();
    <T> void addCriteria(Map<String,List<T>> map, String att, T value) {
        map.computeIfAbsent(att, k -> new ArrayList<>()).add(value);
    }

    Model model;
    public ToolBar(Model model) {
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
        add(new MyButton("Apply", e -> {
            System.out.println(discreteValues.values());
            System.out.println(boundedValues.values());
        }));
    }
}
