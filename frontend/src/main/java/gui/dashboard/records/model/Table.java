package gui.dashboard.records.model;

class Table extends JScrollPane implements ToClear {

    DefaultTableModel model;
    JTable table;

    Table() {

        model = new DefaultTableModel(parser.titleCaseNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return; // ignore intermediate "drag" events
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                form.onSelection(get(selectedRow));
                for (var btn : form.btns) {
                    btn.setEnabled(true);
                }
            }
        });

        Opts.addToClear(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1) {
                    clear();
                }
            }
        });

        loadData();
        setViewportView(table);
    }

    @Override
    public void clear() {
        table.clearSelection();
        for (var btn : form.btns) {
            btn.setEnabled(btn.defaultEnabled);
        }
    }

    orm.Table get(int selectedRow) {
        return orm.Table.search(ORMModelName, "id", (Integer) model.getValueAt(selectedRow, 0)).elementAt(0);
    }

    void loadData() {
        model.setRowCount(0);
        Vector<orm.Table> tuples = orm.Table.search(ORMModelName);
        for (orm.Table tuple : tuples) {
            Object[] row = parser.getAsRow(tuple);
            model.addRow(row);
        }
    }

    void onAdd() {

        var tuple = form.parseFields();
        if (tuple.add() > 0) {
            JOptionPane.showMessageDialog(this, ORMModelName + " added successfully!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
        }
    }

    void onEdit() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {

            var toEdit = get(selectedRow);
            var newValue = form.parseFields();
            for (var field : toEdit.reflect.fields.modifiable()) {
                toEdit.reflect.fields.set(field, newValue.reflect.fields.get(field));
            }

            if (toEdit.edit() > 0) {
                JOptionPane.showMessageDialog(this, "Successfully Edited!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Edit!");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
        }
    }

    void onDelete() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            if (get(selectedRow).delete() > 0) {
                JOptionPane.showMessageDialog(this, "successfully Deleted!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }
}
