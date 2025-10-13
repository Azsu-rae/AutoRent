package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;

import java.util.function.Function;

import gui.component.*;
import gui.util.Attribute;
import gui.Opts;

import static orm.Reflection.getModelInstance;

public class MultipleSelections extends MyDialog<Attribute<String>> {

    JCheckBox[] checkBoxes;
    Attribute<String> attribute;

    public MultipleSelections(String title, Attribute<String> attribute, Function<Attribute<String>,Boolean> callback) {
        super(title, callback);
        this.attribute = attribute;
    }

    @Override
    protected MyPanel initialize() {

        var checkboxPanel = new MyPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        var values = getModelInstance(attribute.ORMModelName).getAttributeValues(attribute.name);
        checkBoxes = new JCheckBox[values.size()];

        int i=0;
        for (var value : values) {
            checkBoxes[i] = new JCheckBox(value);
            checkboxPanel.add(checkBoxes[i]);
            i++;
        }

        var panel = new MyPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        var scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane);
        panel.add(new MyButton("Save", e -> finalize("How can one mess up checking boxes?"), Component.CENTER_ALIGNMENT));

        return panel;
    }

    @Override
    protected Attribute<String> parseInput() {
        for (var checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                attribute.addValue(checkBox.getText());
            }
        } return attribute;
    }
}
