package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.function.Consumer;

import component.*;
import component.Factory.Field;

import util.FieldLabelFormatter;
import util.FieldValueMapper;

import orm.Table;

import static orm.Reflection.fieldsOf;
import static orm.Reflection.getModelInstance;

import static gui.util.FieldValueMapper.parse;
import static gui.component.Factory.createField;

public class RecordEditor extends MyDialog<Table> {

    private Map<String, JTextField> fields = new HashMap<>();

    private Table tuple;
    private String ORMModelName;

    public RecordEditor(String title, String ORMModelName, Consumer<Table> callback, Table tuple) {
        super(title, callback);
        this.ORMModelName = ORMModelName;
        this.tuple = tuple;
    }

    public RecordEditor(String title, String ORMModelName, Consumer<Table> callback) {
        this(title, ORMModelName, callback, null);
    }

    @Override
    protected MyPanel initialize() {

        var modifiableAttributes = fieldsOf(ORMModelName).modifiable().toArray(String[]::new);
        Object[] defaultValues = null;
        if (tuple != null) {
            defaultValues = FieldValueMapper.getModifiablesAsObjects(tuple);
        }

        var fieldsPanel = new MyPanel(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = FieldLabelFormatter.titleCaseNames(modifiableAttributes);
        for (int i = 0; i < labels.length; i++) {

            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            fieldsPanel.add(new MyLabel(labels[i]), gbc);

            JTextField field = createField(20, Field.TEXT);
            if (defaultValues != null) {
                field.setText(defaultValues[i].toString());
            }

            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            fieldsPanel.add(field, gbc);

            fields.put(modifiableAttributes[i], field);
        }

        var panel = new MyPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fieldsPanel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new MyButton("Confirm", _ -> submit("Enter a valid input!")), gbc);

        return panel;
    }

    @Override
    protected boolean validateInput() {
        return true;
    }

    @Override
    protected Table parseInput() {
        Table tuple = getModelInstance(ORMModelName);
        for (var field : fields.entrySet()) {
            var name = field.getKey();
            var value = field.getValue().getText();
            tuple.reflect.fields.set(name, parse(ORMModelName, name, value));
        }
        return tuple;
    }
}
