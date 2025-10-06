package gui.shared;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import gui.util.*;
import gui.util.Listener.Event;

import orm.Table;
import orm.util.Reflection;

public class Grid extends JTable implements ToClear {

    private ListSelectionListener selectionListener;
    private DefaultTableModel defaultTableModel;
    private String ORMModelName;
    private Listener listener;

    public Grid(Listener listener, String ORMModelName) {
        String[] columns = Parser.titleCaseNames(Reflection.getModelInstance(ORMModelName).reflect.fields.names);
        defaultTableModel = new DefaultTableModel(columns, 0) {
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
        selectionListener = e -> {
            if (e.getValueIsAdjusting() || getSelectedRow() == -1) {
                return;
            } listener.onEvent(Event.SELECTION);
        };
        getSelectionModel().addListSelectionListener(selectionListener);
    }

    @Override
    public void clear() {
        clearSelection();
        listener.onEvent(Event.CLEAR);
    }

    @Override
    public void dispose() {
        Opts.removeToClear(this);
        getSelectionModel().removeListSelectionListener(selectionListener);
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
