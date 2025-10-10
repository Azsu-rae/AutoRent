package gui.dashboard.record;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import gui.contract.Listener;
import gui.contract.Listener.Event;
import gui.component.MyButton;
import gui.component.MyPanel;

public class Record extends MyPanel implements Listener {

    List<MyButton> btns = new ArrayList<>();
    TableView tableView;
    ToolBar toolBar;

    public String ORMModelName;
    public Record(String ORMModelName) {
        this.ORMModelName = ORMModelName;

        tableView = new TableView((Listener) this, ORMModelName);
        var buttonPanel = new MyPanel();
        btns.add(new MyButton(buttonPanel, "Add", e -> onAdd(), true));
        btns.add(new MyButton(buttonPanel, "Edit", e -> onEdit(), false));
        btns.add(new MyButton(buttonPanel, "Delete", e -> onDelete(), false));

        toolBar = new ToolBar(ORMModelName, tuples -> {
            if (tuples == null) {
                tableView.loadData();
            } else {
                tableView.loadData(tuples);
            }
        });

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(tableView, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void onEvent(Event e) {
        switch (e) {
            case CLEAR:
                for (var btn : btns) {
                    btn.setEnabled(btn.defaultEnabled);
                } break;
            case SELECTION:
                for (var btn : btns) {
                btn.setEnabled(true);
                } break;
            default:
                break;
        }
    }

    void onAdd() {
//        if (record.recordEditor.parseFields().add() > 0) {
//            JOptionPane.showMessageDialog(this, ORMModelName + " added successfully!");
//            tableView.loadData();
//        } else {
//            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
//        }
    }

    void onEdit() {

//        var toEdit = tableView.parseSelectedRow();
//        var newValue = record.recordEditor.parseFields();
//        for (var field : toEdit.reflect.fields.modifiable()) {
//            toEdit.reflect.fields.set(field, newValue.reflect.fields.get(field));
//        }
//
//        if (toEdit.edit() > 0) {
//            JOptionPane.showMessageDialog(this, "Successfully Edited!");
//            tableView.loadData();
//        } else {
//            JOptionPane.showMessageDialog(this, "Failed to Edit!");
//        }
    }

    void onDelete() {
//        if (tableView.parseSelectedRow().delete() > 0) {
//            JOptionPane.showMessageDialog(this, "successfully Deleted!");
//            tableView.loadData();
//        } else {
//            JOptionPane.showMessageDialog(this, "Failed to delete!");
//        }
    }
}
