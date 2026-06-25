package gui.dashboard.record;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import contract.Listener;
import gui.dashboard.record.dialog.RecordEditor;
import component.MyButton;
import component.MyPanel;

public class Record extends MyPanel implements Listener {

    private ToolBar toolBar;
    public TableView tableView;

    private MyPanel buttonPanel;
    private List<MyButton> btns = new ArrayList<>();

    public String ORMModelName;

    public Record(String ORMModelName, MyPanel buttonPanel) {
        this.ORMModelName = ORMModelName;

        tableView = new TableView(this, ORMModelName);
        toolBar = new ToolBar(ORMModelName, tableView::loadData);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(tableView, BorderLayout.CENTER);
        add(buttonPanel == null ? defaultButtonPanel() : buttonPanel, BorderLayout.SOUTH);
    }

    public Record(String ORMModelName) {
        this(ORMModelName, null);
    }

    @Override
    public void onEvent(Event e) {
        switch (e) {
            case CLEAR:
                for (var btn : btns) {
                    btn.setEnabled(btn.defaultEnabled);
                }
                break;
            case SELECTION:
                for (var btn : btns) {
                    btn.setEnabled(true);
                }
                break;
            default:
                break;
        }
    }

    private void onAdd() {
        String topMsg = String.format("Add a new %s", ORMModelName.toLowerCase());
        new RecordEditor(topMsg, ORMModelName, tuple -> {
            tuple.add();
            tableView.loadData();
            JOptionPane.showMessageDialog(this, ORMModelName + " added successfully!");
        }).display();
    }

    private void onEdit() {
        var toEdit = tableView.parseSelectedRow();
        new RecordEditor("Edit Field", ORMModelName, newValue -> {
            for (var field : toEdit.reflect.fields.modifiable()) {
                toEdit.reflect.fields.set(field, newValue.reflect.fields.get(field));
            }
            toEdit.edit();
            tableView.loadData();
        }, toEdit).display();
    }

    private void onDelete() {
        if (tableView.parseSelectedRow().delete() > 0) {
            JOptionPane.showMessageDialog(this, "successfully Deleted!");
            tableView.loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete!");
        }
    }

    private MyPanel defaultButtonPanel() {
        buttonPanel = new MyPanel();
        btns.add(new MyButton(buttonPanel, "Add", e -> onAdd(), true));
        btns.add(new MyButton(buttonPanel, "Edit", e -> onEdit(), false));
        btns.add(new MyButton(buttonPanel, "Delete", e -> onDelete(), false));
        return buttonPanel;
    }
}
