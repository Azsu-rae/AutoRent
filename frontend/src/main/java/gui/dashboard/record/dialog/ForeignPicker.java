package gui.dashboard.record.dialog;

import gui.dashboard.record.Record;

import component.MyButton;
import component.MyDialog;
import component.MyPanel;
import orm.Table;

import java.util.function.Consumer;

public class ForeignPicker extends MyDialog<Table> {

    private String ORMModelName;
    private Record record;

    public ForeignPicker(String title, String ORMModelName, Consumer<Table> callback) {
        super(title, callback);
        this.ORMModelName = ORMModelName;
    }

    @Override
    protected boolean validateInput() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected MyPanel initialize() {
        var buttonPanel = new MyPanel();
        buttonPanel.add(new MyButton("Select", e -> submit("Again, you can't mess up a selection!")));
        record = new Record(ORMModelName, buttonPanel);
        return record;
    }

    @Override
    protected Table parseInput() {
        return record.tableView.parseSelectedRow();
    }
}
