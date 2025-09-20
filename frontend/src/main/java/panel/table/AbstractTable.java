package panel.table;

import java.util.ArrayList;
import java.util.List;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import orm.Table;
import orm.util.Reflection;
import ui.component.MyPanel;

import static orm.util.Reflection.getModelInstance;

public class AbstractTable extends MyPanel {

    JTable table;
    DefaultTableModel model;

    public AbstractTable(String modelName) {

        model = new DefaultTableModel(columns(getModelInstance(modelName).reflect.fields.names), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable inline editing
            }
        };
        table = new JTable(model);

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private String[] columns(String[] modelColumns) {

        List<String> columns = new ArrayList<>();
        for (String columnName : modelColumns) {
            columnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
            if (Table.hasSubClass(columnName)) {
                columns.add(columnName + " ID");
            } else if (columnName.equals("Id")) {
                columns.add("ID");
            } else {
                columns.add(columnName.replaceAll("([a-z])([A-Z])", "$1 $2"));
            }
        }

        return columns.toArray(String[]::new);
    }
}
