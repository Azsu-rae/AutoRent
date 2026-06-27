package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import component.*;

import static orm.Reflection.getModelInstance;

public class MultipleSelections extends MyDialog<String[]> {

    JCheckBox[] checkBoxes;
    String[] choices;

    public MultipleSelections(String title, String[] choices, Consumer<String[]> callback) {
        super(title, callback);
        this.choices = choices;
    }

    @Override
    protected MyPanel initialize() {

        var checkboxPanel = new MyPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        checkBoxes = new JCheckBox[choices.length];
        for (int i = 0; i < choices.length; i++) {
            checkBoxes[i] = new JCheckBox(choices[i]);
            checkboxPanel.add(checkBoxes[i]);
        }

        var scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        var panel = new MyPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scrollPane);
        panel.add(new MyButton("Save", e -> submit("How can one mess up checking boxes?"), Component.CENTER_ALIGNMENT));

        return panel;
    }

    @Override
    protected boolean validateInput() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected String[] parseInput() {

        var selectedChoices = new ArrayList<>();
        for (var checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedChoices.add(checkBox.getText());
            }
        }

        return selectedChoices.toArray(String[]::new);
    }
}
