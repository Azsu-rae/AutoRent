package gui.dashboard.records.record;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.List;

import gui.component.*;

import static orm.util.Reflection.getModelInstance;

class RecordEditor extends MyPanel {

    Map<String,JTextField> fieldByAtt = new HashMap<>();
    List<MyButton> btns = new ArrayList<>();
    JTextField[] fields;
    String[] labels;
    Record record;

    RecordEditor(Record record) {
        this.record = record;

        var modifiables = record.reflect.fields.modifiable();
        fields = new JTextField[modifiables.size()];
        labels = modifiables.stream().map(record.parser::titleCase).toArray(String[]::new);

        int i=0;
        for (var att : modifiables) {
            fieldByAtt.put(att, fields[i++]);
        }

        var fieldsPanel = Factory.createForm(labels, fields);

        var buttonPanel = new MyPanel();
        btns.add(new MyButton(buttonPanel, "Add", e -> record.recordGrid.onAdd(), true));
        btns.add(new MyButton(buttonPanel, "Edit", e -> record.recordGrid.onEdit(), false));
        btns.add(new MyButton(buttonPanel, "Delete", e -> record.recordGrid.onDelete(), false));
        btns.add(new MyButton(buttonPanel, "Clear", e -> {
            for (var field : fields) {
                field.setText("");
            }
        }, true));

        setLayout(new BorderLayout());
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    void onSelection(orm.Table selectedTuple) {
        for (String att : selectedTuple.reflect.fields.modifiable()) {
            fieldByAtt.get(att).setText(record.parser.getAsColumn(selectedTuple, att).toString());
        }
    }

    orm.Table parseFields() {
        var tuple = getModelInstance(record.ORMModelName);
        for (var field : fieldByAtt.entrySet()) {
            Object value = record.parser.parse(field.getKey(), field.getValue());
            tuple.reflect.fields.callSetter(field.getKey(), value);
        } return tuple;
    }
}
