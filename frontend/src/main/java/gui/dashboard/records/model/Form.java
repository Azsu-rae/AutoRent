package gui.dashboard.records.model;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;

import gui.component.*;

import static orm.util.Reflection.getModelInstance;

class Form extends MyPanel {

    Map<String,JTextField> fields = new HashMap<>();
    List<MyButton> btns = new ArrayList<>();
    Model model;
    Form(Model model) {
        this.model = model;

        var fieldsPanel = new MyPanel();
        fieldsPanel.setLayout(new GridLayout(0, 2, 5, 5));
        for (String ORMColumn : model.reflect.fields.modifiable()) {
            fields.put(ORMColumn, Factory.field(fieldsPanel, model.parser.titleCase(ORMColumn) + ":"));
        }

        var buttonPanel = new MyPanel();
        btns.add(new MyButton(buttonPanel, "Add", e -> model.table.onAdd(), true));
        btns.add(new MyButton(buttonPanel, "Edit", e -> model.table.onEdit(), false));
        btns.add(new MyButton(buttonPanel, "Delete", e -> model.table.onDelete(), false));
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
            fields.get(att).setText(model.parser.getAsColumn(selectedTuple, att).toString());
        }
    }

    orm.Table parseFields() {

        var tuple = getModelInstance(model.ORMModelName);
        for (var field : fields.entrySet()) {
            Object value = model.parser.parse(field.getKey(), field.getValue());
            tuple.reflect.fields.callSetter(field.getKey(), value);
        }

        return tuple;
    }
}
