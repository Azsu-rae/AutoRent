package gui.dashboard.records.model;

import java.awt.event.MouseAdapter;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import gui.util.Opts;
import gui.util.ToClear;

public class RecordGrid extends JScrollPane implements ToClear {

    DefaultTableModel defaultTableModel;
    JTable table;

    Record model;
    RecordGrid(Record model) {
        this.model = model;

        defaultTableModel = new DefaultTableModel(model.parser.titleCaseNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(defaultTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return; // ignore intermediate "drag" events
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                model.recordEditor.onSelection(get(selectedRow));
                for (var btn : model.recordEditor.btns) {
                    btn.setEnabled(true);
                }
            }
        });

        Opts.addToClear(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    clear();
                }
            }
        });

        loadData();
        setViewportView(table);
    }

    @Override
    public void clear() {
        table.clearSelection();
        for (var btn : model.recordEditor.btns) {
            btn.setEnabled(btn.defaultEnabled);
        }
    }

    orm.Table get(int selectedRow) {
        return orm.Table.search(model.ORMModelName, "id", (Integer) defaultTableModel.getValueAt(selectedRow, 0)).elementAt(0);
    }

    public void loadData() {
        loadData(orm.Table.search(model.ORMModelName));
    }

    public void loadData(Vector<orm.Table> tuples) {
        defaultTableModel.setRowCount(0);
        for (var tuple : tuples) {
            Object[] row = model.parser.getAsRow(tuple);
            defaultTableModel.addRow(row);
        }
    }

    void onAdd() {

        var tuple = model.recordEditor.parseFields();
        if (tuple.add() > 0) {
            JOptionPane.showMessageDialog(this, model.ORMModelName + " added successfully!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
        }
    }

    void onEdit() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {

            var toEdit = get(selectedRow);
            var newValue = model.recordEditor.parseFields();
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
