package panel.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.Function;
import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import orm.Table;
import orm.model.Reservation;
import orm.util.Console;
import orm.util.Reflection;
import ui.component.MyPanel;

import static orm.util.Reflection.getModelInstance;

public class AbstractTable extends MyPanel {

    JTable table;
    DefaultTableModel model;

    Map<String,JTextField> fields = new HashMap<>();
    Parser parse = new Parser();
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

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Reservation");
        addButton.addActionListener(e -> addReservation());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteReservation());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(form(), BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
        load(); // Initial data load
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

    private JPanel form() {

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        for (String columnName : modelColumns) {
            if (reflect.fields.hasSetter(columnName)) {
                var field = new JTextField();
                formPanel.add(new JLabel(column(columnName) + ":"));
                formPanel.add(field);
                fields.put(columnName, field);
            }
        }

        return formPanel;
    }

    private void addReservation() {
        try {
            Table tuple = parse.fields();
            if (tuple.add() > 0) {
                JOptionPane.showMessageDialog(this, modelName + " added successfully!");
                load(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            Console.error(e);
        }
    }

    private void deleteReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                Reservation reservation = (Reservation) Table.search(new Reservation().reflect.fields.set("id", id)).elementAt(0);
                if (reservation.delete() > 0) {
                    JOptionPane.showMessageDialog(this, "Reservation deleted successfully!");
                    load();
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

    private void load() {

        // Clear existing rows
        model.setRowCount(0);

        // Fetch data from ORM
        Vector<Table> tuples = Table.search(modelName);
        for (Table tuple : tuples) {
            Object[] row = tuple.reflect.fields.get();
            model.addRow(row);
        }
    }

    class Parser {

        Map<Class<?>,Function<String,Object>> parser = new HashMap<>();
        Parser() {
            parser.put(Integer.class, Integer::parseInt);
            parser.put(Double.class,  Double::parseDouble);
            parser.put(String.class,  String::valueOf);
            parser.put(LocalDate.class,  String::valueOf);
        }

        Table fields() {

            Table tuple = getModelInstance(modelName);
            for (Map.Entry<String,JTextField> field : fields.entrySet()) {
                Object value = field(field.getKey(), field.getValue());
                tuple.reflect.fields.callSetter(field.getKey(), value);
            }

            return tuple;
        }

        Object field(String name, JTextField field) {
            return parser.get(reflect.fields.type(name)).apply(field.getText());
        }
    }
}

