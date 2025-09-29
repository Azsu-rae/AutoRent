package gui.dashboard.records.record.toolbar;

import java.awt.*;

import gui.component.*;
import gui.component.Factory.Field;

class SearchBar extends MyPanel {

    ToolBar toolBar;
    SearchBar(ToolBar toolBar, String[] atts) {
        this.toolBar = toolBar;

        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new MyLabel("Search" + toolBar.model.parser.denominator(atts)));
        var searchField = Factory.createField(20, Field.TEXT);
        add(searchField);
        add(new MyButton("Search", e -> {
            for (var att : atts) {
                toolBar.addDiscreteCriteria(att, searchField.getText());
            } toolBar.onApply();
        }));
    }
}
