package panel.model;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.*;
import java.util.List;
import java.util.function.Function;

import java.time.LocalDate;

import ui.Factory;
import ui.component.*;
import orm.util.*;

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
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) return; // ignore intermediate "drag" events
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    form.onSelection(get(selectedRow));
                    for (var btn : form.btns) {
                        btn.setEnabled(true);
                    }
                }
            });

            loadData();
            setViewportView(table);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row == -1) {
                        table.clearSelection();
                        for (var btn : form.btns) {
                            btn.setEnabled(btn.defaultEnabled);
                        }
                    }
                }
            });
        }

        orm.Table get(int selectedRow) {
            return orm.Table.search(ORMModelName, "id", (Integer) model.getValueAt(selectedRow, 0)).elementAt(0);
        }

        void loadData() {
            model.setRowCount(0);
            Vector<orm.Table> tuples = orm.Table.search(ORMModelName);
            for (orm.Table tuple : tuples) {
                Object[] row = tuple.reflect.fields.getAsRow();
                model.addRow(row);
            }
        }

        void onAdd() {

            var tuple = form.parseFields();
            if (tuple.add() > 0) {
                JOptionPane.showMessageDialog(this, ORMModelName + " added successfully!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
            }
        }

        void onEdit() {

            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {

                var toEdit = get(selectedRow);
                var newValue = form.parseFields();
                for (var field : toEdit.reflect.fields.modifiable()) {
                    toEdit.reflect.fields.set(field, newValue.reflect.fields.get(field));
                }

                if (toEdit.edit() > 0) {
                    JOptionPane.showMessageDialog(this, "Successfully Edited!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to Edit!");
                }

            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            }
        }

        void onDelete() {

            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                if (get(selectedRow).delete() > 0) {
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
        List<MyButton> btns = new ArrayList<>();
        Form() {

            var fieldsPanel = new MyPanel();
            fieldsPanel.setLayout(new GridLayout(0, 2, 5, 5));
            for (String ORMColumn : reflect.fields.modifiable()) {
                fields.put(ORMColumn, Factory.field(fieldsPanel, reflect.fields.titleCase(ORMColumn) + ":"));
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
                fields.get(att).setText(selectedTuple.reflect.fields.getAsColumn(att).toString());
            }
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
