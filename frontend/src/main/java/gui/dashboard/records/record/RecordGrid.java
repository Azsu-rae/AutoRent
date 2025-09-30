package gui.dashboard.records.record;

import java.awt.event.MouseAdapter;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import gui.shared.Grid;
import gui.util.Listener;
import gui.util.Opts;

public class RecordGrid extends JScrollPane {

    Record record;
    Grid grid;

    RecordGrid(Record record) {
        this.record = record;
        grid = new Grid(
            (Listener) record.recordEditor,
            record.ORMModelName,
            record.parser.titleCaseNames(record.reflect.fields.names)
        );

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = grid.rowAtPoint(e.getPoint());
                if (row == -1) {
                    Opts.clearEvent();
                }
            }
        });

        grid.loadData();
        setViewportView(grid);
    }

    void onAdd() {
        if (record.recordEditor.parseFields().add() > 0) {
            JOptionPane.showMessageDialog(this, record.ORMModelName + " added successfully!");
            grid.loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
        }
    }

    void onEdit() {

        var toEdit = grid.parseSelectedRow();
        var newValue = record.recordEditor.parseFields();
        for (var field : toEdit.reflect.fields.modifiable()) {
            toEdit.reflect.fields.set(field, newValue.reflect.fields.get(field));
        }

        if (toEdit.edit() > 0) {
            JOptionPane.showMessageDialog(this, "Successfully Edited!");
            grid.loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to Edit!");
        }
    }

    void onDelete() {
        if (grid.parseSelectedRow().delete() > 0) {
            JOptionPane.showMessageDialog(this, "successfully Deleted!");
            grid.loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete!");
        }
    }
}
