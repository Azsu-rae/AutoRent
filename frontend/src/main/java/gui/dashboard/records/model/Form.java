package panel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextField;

import ui.Factory;
import ui.component.MyButton;
import ui.component.MyPanel;

public class Form {

    Map<String,JTextField> fields = new HashMap<>();
    List<MyButton> btns = new ArrayList<>();
    Form() {

        var fieldsPanel = new MyPanel();
        fieldsPanel.setLayout(new GridLayout(0, 2, 5, 5));
        for (String ORMColumn : reflect.fields.modifiable()) {
            fields.put(ORMColumn, Factory.field(fieldsPanel, parser.titleCase(ORMColumn) + ":"));
        }

        var buttonPanel = new MyPanel();
        btns.add(new MyButton(buttonPanel, "Add", e -> table.onAdd(), true));
        btns.add(new MyButton(buttonPanel, "Edit", e -> table.onEdit(), false));
        btns.add(new MyButton(buttonPanel, "Delete", e -> table.onDelete(), false));
        btns.add(new MyButton(buttonPanel, "Clear", e -> {
            for (var field : fields.values()) {
                field.setText("");
            }
        }, true));

        setLayout(new BorderLayout());
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    void onSelection(orm.Table selectedTuple) {
        for (String att : selectedTuple.reflect.fields.modifiable()) {
            fields.get(att).setText(parser.getAsColumn(selectedTuple, att).toString());
        }
    }

    orm.Table parseFields() {

        orm.Table tuple = getModelInstance(ORMModelName);
        for (Map.Entry<String,JTextField> field : fields.entrySet()) {
            Object value = parser.parse(field.getKey(), field.getValue());
            tuple.reflect.fields.callSetter(field.getKey(), value);
        }

        return tuple;
    }
}
