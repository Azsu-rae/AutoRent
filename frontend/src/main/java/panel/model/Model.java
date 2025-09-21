package panel.model;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.*;
import java.util.function.Function;

import java.time.LocalDate;

import ui.component.*;
import orm.util.*;

import ui.Factory;
import ui.Factory.Field;

import static orm.util.Reflection.getModelInstance;

public class Model extends MyPanel {

    String ORMModelName;
    Reflection reflect;

    Table table;
    Form form;

    public Model(String ORMModelName) {
        this.ORMModelName = ORMModelName;

        reflect = getModelInstance(ORMModelName).reflect;
        table = new Table();
        form = new Form();

        setLayout(new BorderLayout());
        add(table, BorderLayout.CENTER);
        add(form, BorderLayout.SOUTH);
    }

    class Table extends JScrollPane {

        DefaultTableModel model;
        JTable table;

        Table() {

            model = new DefaultTableModel(reflect.fields.titleCaseNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);

            setViewportView(table);
            loadData();
        }

        private void loadData() {

            model.setRowCount(0);
            Vector<orm.Table> tuples = orm.Table.search(ORMModelName);
            for (orm.Table tuple : tuples) {
                Object[] row = tuple.reflect.fields.get();
                model.addRow(row);
            }
        }

        private void onAdd() {

            orm.Table tuple = form.parseFields();
            if (tuple != null && tuple.add() > 0) {
                JOptionPane.showMessageDialog(this, ORMModelName + " added successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
            }
        }

        private void onDelete() {

            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                var tuple = orm.Table.search(ORMModelName, "id", id).elementAt(0);
                if (tuple.delete() > 0) {
                    JOptionPane.showMessageDialog(this, "successfully Deleted!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        }
    }

    class Form extends MyPanel {

        static Map<Class<?>,Function<String,Object>> parser = new HashMap<>();
        static {
            parser.put(Integer.class, Integer::parseInt);
            parser.put(Double.class,  Double::parseDouble);
            parser.put(String.class, s -> s.equals("") ? null : s);
            parser.put(LocalDate.class,  s -> orm.Table.stringToDate(s));
        }

        Map<String,JTextField> fields = new HashMap<>();
        Form() {

            var fieldsPanel = new MyPanel();
            fieldsPanel.setLayout(new GridLayout(0, 2, 5, 5));
            for (String ORMColumn : reflect.fields.names) {
                if (reflect.fields.hasSetter(ORMColumn)) {
                    addField(fieldsPanel, ORMColumn);
                }
            }

            var buttonPanel = new MyPanel();
            addButton(buttonPanel, "Add", e -> table.onAdd());
            addButton(buttonPanel, "Delete", e -> table.onDelete());

            setLayout(new BorderLayout());
            add(fieldsPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        void addField(MyPanel panel, String ORMColumn) {
            var field = Factory.field(Field.TEXT);
            panel.add(new MyLabel(reflect.fields.titleCase(ORMColumn) + ":"));
            panel.add(field);
            fields.put(ORMColumn, field);
        }

        void addButton(MyPanel panel, String name, ActionListener l) {
            var btn = new MyButton(name);
            btn.addActionListener(l);
            panel.add(btn);
        }

        orm.Table parseFields() {

            orm.Table tuple = getModelInstance(ORMModelName);
            for (Map.Entry<String,JTextField> field : fields.entrySet()) {
                Object value = getValue(field.getKey(), field.getValue());
                tuple.reflect.fields.callSetter(field.getKey(), value);
            }

            return tuple;
        }

        Object getValue(String name, JTextField field) {
            try {
                return parser.get(reflect.fields.type(name)).apply(field.getText());
            } catch (Exception e) {
                return null;
            }
        }
    }
}
