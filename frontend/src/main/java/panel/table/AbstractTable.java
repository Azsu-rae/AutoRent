package panel.table;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.*;
import java.util.List;
import java.util.function.Function;

import java.time.LocalDate;

import orm.Table;
import orm.util.*;

import ui.component.*;
import ui.Factory;
import ui.Factory.Field;

import static orm.util.Reflection.getModelInstance;

public class AbstractTable extends MyPanel {

    Form form;
    JTable table;
    DefaultTableModel model;

    String[] modelColumns;
    Reflection reflect;
    String modelName;

    public AbstractTable(String modelName) {
        this.modelName = modelName;

        reflect = getModelInstance(modelName).reflect;
        modelColumns = reflect.fields.names;

        model = new DefaultTableModel(columns(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable inline editing
            }
        };
        table = new JTable(model);
        form = new Form();

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(form, BorderLayout.SOUTH);
        loadData(); // Initial data load
    }

    private String[] columns() {

        List<String> columns = new ArrayList<>();
        for (String columnName : modelColumns) {
            columns.add(column(columnName));
        }

        return columns.toArray(String[]::new);
    }

    private String column(String columnName) {
        columnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
        if (Table.hasSubClass(columnName)) {
            return columnName + " ID";
        } else if (columnName.equals("Id")) {
            return "ID";
        } else {
            return columnName.replaceAll("([a-z])([A-Z])", "$1 $2");
        }
    }

    private void loadData() {

        // Clear existing rows
        model.setRowCount(0);

        // Fetch data from ORM
        Vector<Table> tuples = Table.search(modelName);
        for (Table tuple : tuples) {
            Object[] row = tuple.reflect.fields.get();
            model.addRow(row);
        }
    }

    class Form extends MyPanel {

        static Map<Class<?>,Function<String,Object>> parser = new HashMap<>();
        static {
            parser.put(Integer.class, Integer::parseInt);
            parser.put(Double.class,  Double::parseDouble);
            parser.put(String.class,  String::valueOf);
            parser.put(LocalDate.class,  String::valueOf);
        }

        Map<String,JTextField> fields = new HashMap<>();

        Form() {

            var fieldsPanel = new MyPanel();
            fieldsPanel.setLayout(new GridLayout(0, 2, 5, 5));
            for (String columnName : modelColumns) {
                if (reflect.fields.hasSetter(columnName)) {
                    var field = Factory.field(Field.TEXT);
                    fieldsPanel.add(new MyLabel(column(columnName) + ":"));
                    fieldsPanel.add(field);
                    fields.put(columnName, field);
                }
            }

            var buttonPanel = new MyPanel();
            addButton(buttonPanel, "Add", e -> add());
            addButton(buttonPanel, "Delete", e -> delete());

            setLayout(new BorderLayout());
            add(fieldsPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        void addButton(MyPanel panel, String name, ActionListener l) {
            var btn = new MyButton(name);
            btn.addActionListener(l);
            panel.add(btn);
        }

        Table parseFields() {

            Table tuple = getModelInstance(modelName);
            for (Map.Entry<String,JTextField> field : fields.entrySet()) {
                Object value = getValue(field.getKey(), field.getValue());
                tuple.reflect.fields.callSetter(field.getKey(), value);
            }

            return tuple;
        }

        Object getValue(String name, JTextField field) {
            return parser.get(reflect.fields.type(name)).apply(field.getText());
        }

        private void add() {
            try {
                Table tuple = parseFields();
                if (tuple.add() > 0) {
                    JOptionPane.showMessageDialog(this, modelName + " added successfully!");
                    loadData(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                Console.error(e);
            }
        }

        private void delete() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int id = (Integer) model.getValueAt(selectedRow, 0);
                    var reservation = Table.search(getModelInstance(modelName).reflect.fields.set("id", id)).elementAt(0);
                    if (reservation.delete() > 0) {
                        JOptionPane.showMessageDialog(this, "Reservation deleted successfully!");
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete reservation.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                    Console.error(e);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a reservation to delete.");
            }
        }
    }
}
