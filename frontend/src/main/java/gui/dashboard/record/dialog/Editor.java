package gui.dashboard.record.dialog;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.util.function.Function;

import gui.component.*;
import gui.util.Parser;

import orm.Table;

import static orm.Reflection.fieldsOf;
import static orm.Reflection.getModelInstance;
import static gui.util.Parser.getAsRow;
import static gui.util.Parser.parse;

public class Editor extends MyDialog<Table> {

    private Map<String,JTextField> fields = new HashMap<>();

    private Table tuple;
    private String ORMModelName;
    public Editor(String title, String ORMModelName, Table tuple, Function<Table,Boolean> callback) {
        super(title, callback);
        this.ORMModelName = ORMModelName;
        this.tuple = tuple;
    }

    public Editor(String title, String ORMModelName, Function<Table,Boolean> callback) {
        this(title, ORMModelName, null, callback);
    }

    @Override
    protected MyPanel initialize() {

        Object[] defaultValues = null;
        var modifiables = fieldsOf(ORMModelName).modifiable();
        if (tuple != null) {
            defaultValues = getAsRow(tuple);
        }

        String[] labels = Parser.titleCaseNames(modifiables.toArray(String[]::new));
        var fieldsPanel = Factory.createForm(labels, fields, defaultValues);

        var panel = new MyPanel(new GridBagLayout());
        var gbc = Factory.initFormGBC();

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(fieldsPanel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new MyButton("Confirm", e -> finalize("I really have to get to giving meaningful error messages")), gbc);

        return panel;
    }

    @Override
    protected Table parseInput() {
        Table tuple = getModelInstance(ORMModelName);
        for (var field : fields.entrySet()) {
            var name = field.getKey();
            var value = field.getValue().getText();
            tuple.reflect.fields.set(name, parse(ORMModelName, name, value));
        } return tuple;
    }
}
