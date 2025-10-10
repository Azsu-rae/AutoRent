package gui.dashboard.record;

import java.awt.event.MouseAdapter;
import java.util.Vector;

import javax.swing.*;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import gui.Opts;
import gui.contract.*;
import gui.contract.Listener.Event;
import gui.util.Parser;
import orm.Table;
import orm.Reflection;

public class TableView extends JScrollPane implements ToClear {

    private ListSelectionListener selectionListener;
    private DefaultTableModel defaultTableModel;
    private JTable table;


    private String ORMModelName;
    private Listener listener;
    public TableView(Listener listener, String ORMModelName) {

        this.ORMModelName = ORMModelName;
        this.listener = listener;
        Opts.addToClear(this);

        String[] columns = Parser.titleCaseNames(Reflection.getModelInstance(ORMModelName).reflect.fields.names);
        defaultTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable();
        table.setModel(defaultTableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionListener = e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() == -1) {
                return;
            } listener.onEvent(Event.SELECTION);
        };
        table.getSelectionModel().addListSelectionListener(selectionListener);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    Opts.clearEvent();
                }
            }
        });

        loadData();
        setViewportView(table);
    }

    @Override
    public void clear() {
        table.clearSelection();
        listener.onEvent(Event.CLEAR);
    }

    @Override
    public void dispose() {
        Opts.removeToClear(this);
        table.getSelectionModel().removeListSelectionListener(selectionListener);
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

        int selected = table.getSelectedRow();
        if (selected >= 0) {
            var tupleID = (Integer) defaultTableModel.getValueAt(selected, 0);
            return Table.search(ORMModelName, "id", tupleID).elementAt(0);
        }

        String s = "Attempting to parse when no row is selected! Selected row: %d";
        throw new IllegalStateException(String.format(s, table.getSelectedRow()));
    }
}
