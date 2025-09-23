package ui.component;

import static orm.util.Reflection.getModelInstance;

import java.awt.*;
import javax.swing.*;

import util.*;
import util.Listener.Event;

public class MultipleSelections extends JDialog implements Source, Filter {

    JCheckBox[] checkBoxes;
    String att;

    Listener listener;
    public MultipleSelections(Listener listener, String title, String ORMModel, String att) {
        super(Opts.MAIN_FRAME, title, true);
        this.listener = listener;
        this.att = att;

        var checkboxPanel = new MyPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        var values = getModelInstance(ORMModel).getAttributeValues(att);
        checkBoxes = new JCheckBox[values.size()];

        int i=0;
        for (var value : values) {
            checkBoxes[i] = new JCheckBox(value);
            checkboxPanel.add(checkBoxes[i]);
            i++;
        }

        add(new JScrollPane(checkboxPanel));
        add(new MyButton("Apply", e -> notifyListener(Event.DISCRETE)));
        setVisible(true);
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }

    @Override
    public Object getCriteria(Event event) {

        if (event.equals(Event.DISCRETE)) {
            throw new IllegalArgumentException(String.format("Wront event! Should be DISCRETE, was %s", event.name()));
        }

        java.util.List<String> selectedItems = new java.util.ArrayList<>();
        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                selectedItems.add(checkBox.getText());
            }
        }

        dispose();
        return new Filter.DiscreteValues(att, selectedItems.toArray(Object[]::new));
    }
}
