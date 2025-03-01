package dashboard.panel.table;

import orm.Table;
import orm.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AbstractTablePanel extends JPanel {

    protected JTable table;
    protected DefaultTableModel tableModel;
    protected JPanel buttonPanel;

    public AbstractTablePanel(String title, String[] columnNames) {

        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            title, 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.PLAIN, 14)
        ));

        tableSetup(columnNames);
        buttonSetup();

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    protected abstract void loadTableData();

    protected abstract void onAdd();

    protected abstract void onEdit(int selectedRow);

    protected abstract void onDelete(int selectedRow);

    public JTable getTable() {

        return table;
    }

    private void tableSetup(String[] columnNames) {

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        table = new JTable(tableModel);
        table.setDefaultEditor(Object.class, null);
    }

    private void buttonSetup() {

        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit(table.getSelectedRow()));
        deleteButton.addActionListener(e -> onDelete(table.getSelectedRow()));

        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            editButton.setEnabled(selectedRows.length == 1); 
            deleteButton.setEnabled(selectedRows.length == 1); 
        });

        buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
    }
}
