package bex;

import aex.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.ArrayList;

public class VehiculePanel extends AbstractTablePanel {

    private List<Vehicule> vehicules;
    private List<List<String>> filters;
    private List<Pair<String,String>> rangeFilters;

    public VehiculePanel() {

        super("Vehicules", new String[]{"ID", "Brand", "Model", "Year", "Price Per Day", "State", "Maintenance Date", 
            "Type", "Fuel"});

        loadTableData();
        setFilters();
    }

    @Override
    protected void loadTableData() {

        loadVehicules(UserInterface.getAllVehicules());
    }

    @Override
    protected void onAdd() {

        VehiculeDialog dialog = new VehiculeDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Vehicule v = dialog.getVehiculeData();
            if (Vehicule.add(v)) {
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Fields Maintenance Date, Status and Price/Date cannot be empty and must have a valid format.");
            }
        }
    }

    @Override
    protected void onEdit(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        Vehicule selectedVehicule = getSelectedVehicule(selectedRow);
        VehiculeDialog dialog = new VehiculeDialog(SwingUtilities.getWindowAncestor(this), selectedVehicule);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Vehicule v = dialog.getVehiculeData();
            if (UserInterface.editVehicule(selectedVehicule, v)) {
                updateRow(v, selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Fields Maintenance Date, Status and Price/Date cannot be empty and must have a valid format.");
            }
        }
    }

    @Override
    protected void onDelete(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        Vehicule selectedVehicule = getSelectedVehicule(selectedRow);
        List<Reservation> rs = UserInterface.getVehiculeReservations(selectedVehicule);
        if (rs.size() > 0) {

            int response = JOptionPane.showConfirmDialog(
                this, 
                "This vehicule has ongoing reservations, are you sure you want to delete it?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );

            if (response != JOptionPane.YES_OPTION) {
                return;
            }
        }

        if (UserInterface.deleteVehicule(selectedVehicule)) {
            JOptionPane.showMessageDialog(this, "Vehicule deleted successfully.");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Error in Vehicule deletion.");
        }
    }

    private void updateRow(Vehicule v, int selectedRow) {

        tableModel.setValueAt(v.getBrand(), selectedRow, 0);
        tableModel.setValueAt(v.getModel(), selectedRow, 1);
        tableModel.setValueAt(v.getYear(), selectedRow, 2);
        tableModel.setValueAt(v.getPricePerDay(), selectedRow, 3);
        tableModel.setValueAt(v.getState(), selectedRow, 4);
        tableModel.setValueAt(v.getMaintenanceDate(), selectedRow, 5);
        tableModel.setValueAt(v.getVehiculeType(), selectedRow, 6);
        tableModel.setValueAt(v.getFuelType(), selectedRow, 7);
    }

    private void setFilters() {

        setFilterVariables();

        JToolBar filterToolbar = new JToolBar();

        JButton brandButton = new JButton("Filter by Brand");
        brandButton.addActionListener(e -> showMultiSelectDialog("Select Brands", "Brand"));
        filterToolbar.add(brandButton);

        JButton modelButton = new JButton("Filter by Model");
        modelButton.addActionListener(e -> showMultiSelectDialog("Select Models", "Model"));
        filterToolbar.add(modelButton);

        JButton yearButton = new JButton("Filter by Year");
        yearButton.addActionListener(e -> showRangeDialog("Year Range", "Min Year", "Max Year"));
        filterToolbar.add(yearButton);

        JButton priceButton = new JButton("Filter by Price");
        priceButton.addActionListener(e -> showRangeDialog("Price Range", "Min Price", "Max Price"));
        filterToolbar.add(priceButton);

        JButton dateButton = new JButton("Filter by Maintenance Date");
        dateButton.addActionListener(e -> showRangeDialog("Maintenance Date Range", "Start Date (YYYY-MM-DD)", 
                    "End Date (YYYY-MM-DD)"));
        filterToolbar.add(dateButton);

        JButton typeButton = new JButton("Filter by Type");
        typeButton.addActionListener(e -> showMultiSelectDialog("Select Types", "Type"));
        filterToolbar.add(typeButton);

        JButton fuelButton = new JButton("Filter by Fuel");
        fuelButton.addActionListener(e -> showMultiSelectDialog("Select Fuels", "Fuel"));
        filterToolbar.add(fuelButton);

        JButton availabilityButton = new JButton("Filter by Availability");
        availabilityButton.addActionListener(e -> showRangeDialog("Availability Period", "Start Date (YYYY-MM-DD)", 
                    "End Date (YYYY-MM-DD)"));
        filterToolbar.add(availabilityButton);

        JButton resetButton = new JButton("Reset Filters");
        resetButton.addActionListener(e -> resetFilters());
        filterToolbar.add(resetButton);

        JButton applyButton = new JButton("Apply Filters");
        applyButton.addActionListener(e -> applyFilters());
        filterToolbar.add(applyButton);

        add(filterToolbar, BorderLayout.NORTH);
    }

    protected void loadVehicules(List<Vehicule> vs) {

        vehicules = vs;
        tableModel.setRowCount(0);
        for (Vehicule v : vs) {
            newRow(v);
        }
    }

    public Vehicule getSelectedVehicule(int selectedRow) {

        return vehicules.get(selectedRow);
    }

    private void newRow(Vehicule v) {

        tableModel.addRow(new Object[]{v.getId(), v.getBrand(), v.getModel(), v.getYear(), v.getPricePerDay(), v.getState(), 
            v.getMaintenanceDate(), v.getVehiculeType(), v.getFuelType()});
    }

    private void showMultiSelectDialog(String title, String filterType) {

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout());

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        String[] items = UserInterface.getVehiculeFilterOptions(filterType);

        JCheckBox[] checkBoxes = new JCheckBox[items.length];
        for (int i=0;i<items.length;i++) {
            checkBoxes[i] = new JCheckBox(items[i]);
            checkboxPanel.add(checkBoxes[i]);
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton applyButton = new JButton("Save");
        applyButton.addActionListener(e -> {
            List<String> selectedItems = new java.util.ArrayList<>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    selectedItems.add(checkBox.getText());
                }
            }
            saveFilter(filterType, selectedItems);
            dialog.dispose();
        });
        dialog.add(applyButton, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void showRangeDialog(String title, String minLabel, String maxLabel) {

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(310, 150);
        dialog.setLayout(new GridLayout(3, 1, 5, 5));

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(1,2,5,5));
        p1.add(new JLabel(minLabel + ":"));
        JTextField minField = new JTextField(10);
        p1.add(minField);
        dialog.add(p1);

        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1,2,5,5));
        p2.add(new JLabel(maxLabel + ":"));
        JTextField maxField = new JTextField(10);
        p2.add(maxField);
        dialog.add(p2);

        JButton applyButton = new JButton("Save");
        applyButton.addActionListener(e -> {
            String minValue = minField.getText().trim();
            String maxValue = maxField.getText().trim();
            saveRangeFilter(title, minValue, maxValue);
            dialog.dispose();
        });
        dialog.add(panel(applyButton));

        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private JPanel panel(Component p) {

        JPanel panel = new JPanel();
        panel.add(p);
        return panel;
    }

    private void saveFilter(String filterType, List<String> selectedItems) {

        int i = -1;
        switch (filterType) {
            case "Brand":
                i = 0;
                break;
            case "Model":
                i = 1;
                break;
            case "Type":
                i = 2;
                break;
            case "Fuel":
                i = 3;
                break;
            default:
        }

        filters.add(i, selectedItems);
    }

    private void saveRangeFilter(String title, String minValue, String maxValue) {

        int i=-1;
        switch (title) {
            case "Year Range":
                i=0;
                break;
            case "Price Range":
                i=1;
                break;
            case "Maintenance Date Range":
                i=2;
                break;
            case "Availability Period":
                i=3;
                break;
            default:
        }

        rangeFilters.add(i, new Pair<>(minValue,maxValue));
    }

    private void applyFilters() {

        loadVehicules(UserInterface.filterVehicules(filters, rangeFilters));
    }

    private void resetFilters() {

        loadTableData();
        setFilterVariables();
    }

    private void setFilterVariables() {

        filters = new ArrayList<>();
        filters.add(new ArrayList<>());
        filters.add(new ArrayList<>());
        filters.add(new ArrayList<>());
        filters.add(new ArrayList<>());

        rangeFilters = new ArrayList<>();
        rangeFilters.add(new Pair<>());
        rangeFilters.add(new Pair<>());
        rangeFilters.add(new Pair<>());
        rangeFilters.add(new Pair<>());
    }
}


class VehiculeDialog extends JDialog {

    private JTextField brandField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField pricePerDayField;
    private JComboBox<String> stateComboBox;
    private JTextField maintenanceDateField;
    private JTextField typeField;
    private JTextField fuelField;
    private boolean confirmed;

    public VehiculeDialog(Window parent, Vehicule vehicule) {

        super(parent, "Vehicule Details", ModalityType.APPLICATION_MODAL);
        initialize(vehicule);
    }

    private void initialize(Vehicule vehicule) {

        setTitle(vehicule == null ? "Add Vehicule" : "Edit Vehicule");
        setSize(400, 300);
        setLayout(new GridLayout(9, 2, 5, 5));
        setLocationRelativeTo(null);

        add(new JLabel("Brand:"));
        brandField = new JTextField(vehicule == null ? "" : vehicule.getBrand());
        add(brandField);

        add(new JLabel("Model:"));
        modelField = new JTextField(vehicule == null ? "" : vehicule.getModel());
        add(modelField);

        add(new JLabel("Year:"));
        yearField = new JTextField(vehicule == null ? "" : vehicule.getYear().toString());
        add(yearField);

        add(new JLabel("Price Per Day:"));
        pricePerDayField = new JTextField(vehicule == null ? "" : vehicule.getPricePerDay().toString());
        add(pricePerDayField);

        add(new JLabel("State:"));
        stateComboBox = new JComboBox<>(new String[]{"Available", "Unavailable", "Rented"});
        stateComboBox.setSelectedItem(vehicule == null ? "Available" : vehicule.getState());
        add(stateComboBox);

        add(new JLabel("Maintenance Date (YYYY-MM-DD):"));
        maintenanceDateField = new JTextField(vehicule == null ? "" : vehicule.getMaintenanceDate());
        add(maintenanceDateField);

        add(new JLabel("Type:"));
        typeField = new JTextField(vehicule == null ? "" : vehicule.getVehiculeType());
        add(typeField);

        add(new JLabel("Fuel:"));
        fuelField = new JTextField(vehicule == null ? "" : vehicule.getFuelType());
        add(fuelField);

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> { 
                confirmed = true;
                dispose();
        });

        cancelButton.addActionListener(e -> {
                confirmed = false;
                dispose();
        });

        add(confirmButton);
        add(cancelButton);
    }

    public boolean isConfirmed() {

        return confirmed;
    }

    public Vehicule getVehiculeData() {

        if (!confirmed) {
            return null;
        }

        String brand = format(brandField.getText().trim());
        String model = format(modelField.getText().trim());
        String type = format(typeField.getText().trim());
        String fuel = format(fuelField.getText().trim());

        Integer year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (Exception e) {
            year = null;
        }

        Double pricePerDay; 
        try {
            pricePerDay = Double.parseDouble(pricePerDayField.getText().trim());
        } catch (Exception e) {
            pricePerDay = null;
        }

        String state = (String) stateComboBox.getSelectedItem();
        String maintenanceDate = validDate(maintenanceDateField.getText().trim());

        return new Vehicule(pricePerDay, state, maintenanceDate, year, brand, model, type, fuel);
    }

    private String validDate(String dateStr) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(dateStr, formatter).toString(); 
        } catch (Exception e) {
            return null; 
        }
    }

    private String format(String s) {

        return s.equals("") ? null : s;
    }
}
