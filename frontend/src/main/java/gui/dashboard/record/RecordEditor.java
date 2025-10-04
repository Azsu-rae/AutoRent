package gui.dashboard.record;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import gui.component.*;
import gui.util.Parser;
import orm.Table;

import static orm.util.Reflection.getModelInstance;

class RecordEditor extends MyPanel implements gui.util.Listener {

    Map<String,JTextField> fieldByAtt = new HashMap<>();
    String[] labels;

    List<MyButton> btns = new ArrayList<>();
    JTextField[] fields;

    Record record;
    RecordEditor(Record record) {
        this.record = record;

        var modifiables = record.reflect.fields.modifiable();
        fields = new JTextField[modifiables.size()];
        labels = modifiables.stream().map(record.parser::titleCase).toArray(String[]::new);
        var fieldsPanel = Factory.createForm(labels, fields);

        int i=0;
        for (var att : modifiables) {
            fieldByAtt.put(att, fields[i++]);
        }

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

    Table parseFields() {
        var tuple = getModelInstance(record.ORMModelName);
        for (var field : fieldByAtt.entrySet()) {
            Object value = record.parser.parse(field.getKey(), field.getValue());
            tuple.reflect.fields.callSetter(field.getKey(), value);
        } return tuple;
    }

    @Override
    public void onEvent(Event e) {
        switch (e) {
            case CLEAR:
                for (var btn : btns) {
                    btn.setEnabled(btn.defaultEnabled);
                } break;
            case SELECTION:
                var selected = record.recordGrid.grid.parseSelectedRow();
                for (String att : record.reflect.fields.modifiable()) {
                    fieldByAtt.get(att).setText(Parser.getAsColumn(selected, att).toString());
                }  for (var btn : btns) {
                btn.setEnabled(true);
                } break;
            default:
                break;
        }
    }
}
