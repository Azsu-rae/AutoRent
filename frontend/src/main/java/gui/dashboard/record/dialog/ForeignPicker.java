package gui.dashboard.record.dialog;

import gui.component.MyButton;
import gui.component.MyDialog;
import gui.component.MyPanel;
import gui.contract.Listener;
import gui.dashboard.record.TableView;
import orm.Table;

import java.awt.*;
import java.util.function.Function;

public class ForeignPicker extends MyDialog<Table> implements Listener {

    private String ORMModelName;
    public ForeignPicker(String title, String ORMModelName, Function<Table,Boolean> callback) {
        super(title, callback);
        this.ORMModelName = ORMModelName;
    }

    @Override
    protected MyPanel initialize() {
        var panel = new MyPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new TableView(this, ORMModelName), BorderLayout.CENTER);
        panel.add(new MyButton("Select", e -> finalize("Again, you can't mess up a selection!")));
        return panel;
    }

    @Override
    protected Table parseInput() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onEvent(Event e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
    }
}
