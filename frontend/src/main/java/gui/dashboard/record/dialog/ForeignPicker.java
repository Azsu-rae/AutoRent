package gui.dashboard.record.dialog;

import gui.dashboard.record.Record;

import gui.component.MyButton;
import gui.component.MyDialog;
import gui.component.MyPanel;
import orm.Table;

import java.util.function.Function;

public class ForeignPicker extends MyDialog<Table> {

    private String ORMModelName;
    private Record record;

    public ForeignPicker(String title, String ORMModelName, Function<Table,Boolean> callback) {
        super(title, callback);
        this.ORMModelName = ORMModelName;
    }

    @Override
    protected MyPanel initialize() {
        var buttonPanel = new MyPanel();
        buttonPanel.add(new MyButton("Select", e -> finalize("Again, you can't mess up a selection!")));
        record = new Record(ORMModelName, buttonPanel);
        return record;
    }

    @Override
    protected Table parseInput() {
        return record.tableView.parseSelectedRow();
    }
}
