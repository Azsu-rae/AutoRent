package ui.component;

import static orm.util.Reflection.getModelInstance;

import java.awt.*;
import javax.swing.*;

import util.*;
import util.Listener.Event;

public class MultipleSelections extends JDialog implements Source {
    public MultipleSelections(String title, String ORMModel, String att) {
        super(Opts.MAIN_FRAME, title, true);

        MyPanel checkboxPanel = new MyPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        var values = getModelInstance(ORMModel).getAttributeValues(att);
        var checkBoxes = new JCheckBox[values.size()];

        int i=0;
        for (var value : values) {
            checkBoxes[i] = new JCheckBox(value);
            checkboxPanel.add(checkBoxes[i]);
            i++;
        }

        add(new JScrollPane(checkboxPanel));
        add(new MyButton("Apply", e -> notifyListener(Event.APPLIED)));
    }

    @Override
    public void notifyListener(Event event) {

    }
}
