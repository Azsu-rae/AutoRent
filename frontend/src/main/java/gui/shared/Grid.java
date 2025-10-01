package gui.shared;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import gui.util.*;
import gui.util.Listener.Event;

import orm.Table;

public class Grid extends JTable implements ToClear, Source {

    private DefaultTableModel defaultTableModel;
    private String ORMModelName;
    private Listener listener;

    public Grid(Listener listener, String ORMModelName, String[] columnNames) {
        defaultTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        setModel(defaultTableModel);

        this.ORMModelName = ORMModelName;
        this.listener = listener;
        Opts.addToClear(this);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return; // ignore intermediate "drag" events
            } notifyListener(Event.SELECTION);
        });
    }

    @Override
    public void clear() {
        clearSelection();
        notifyListener(Event.CLEAR);
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }

    public void loadData() {
        loadData(Table.search(ORMModelName));
    }

    public void loadData(Vector<Table> tuples) {
        defaultTableModel.setRowCount(0);
        for (var tuple : tuples) {
            Object[] row = Parser.getAsRow(tuple);
            defaultTableModel.addRow(row);
        }
    }

    public Table parseSelectedRow() {

        int selected = getSelectedRow();
        if (selected >= 0) {
            var tupleID = (Integer) defaultTableModel.getValueAt(selected, 0);
            return Table.search(ORMModelName, "id", tupleID).elementAt(0);
        }

        String s = "Attempting to parse when no row is selected! Selected row: %d";
        throw new IllegalStateException(String.format(s, getSelectedRow()));
    }
}
