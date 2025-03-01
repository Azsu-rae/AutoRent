package dashboard.panel.table;

import orm.Table;
import orm.model.*;

import javax.swing.*;
import java.awt.*;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Vector;

public class ReservationPanel extends AbstractTablePanel {

    private Vector<Reservation> reservations;

    private JComboBox<String> returnStateBox, reservationStatusBox, paymentStateBox;
    private Vector<Pair<String,String>> filters;
    private Client client;
    private Vehicule vehicule;

    public ReservationPanel() {

        super("Reservations",new String[]{"ID","Client ID","Vehicule ID","Total Amount","Start Date","End Date","Status"});

        loadTableData();
        setFilters();
        setButtons();
    }

    @Override
    protected void loadTableData() {

        loadReservations(UserInterface.getAllReservations());
    }

    @Override
    protected void onAdd() {

        ReservationDialog dialog = new ReservationDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Reservation r = dialog.getReservationData();
            if (Reservation.add(r)) {
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }
    }

    @Override
    protected void onEdit(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        Reservation selectedReservation = getSelectedReservation(selectedRow);
        ReservationDialog dialog = new ReservationDialog(SwingUtilities.getWindowAncestor(this), selectedReservation);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Reservation r = dialog.getReservationData();
            if (UserInterface.editReservation(selectedReservation, r)) {
                updateRow(r, selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }
    }

    @Override
    protected void onDelete(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }

        Reservation selectedReservation = getSelectedReservation(selectedRow);
        if (UserInterface.deleteReservation(selectedReservation)) {
            JOptionPane.showMessageDialog(this, "Reservation deleted successfully.");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Error in Reservation deletion.");
        }
    }

    private void updateRow(Reservation r, int selectedRow) {

        tableModel.setValueAt(r.getId(), selectedRow, 0);
        tableModel.setValueAt(r.getClient().getId(), selectedRow, 1);
        tableModel.setValueAt(r.getVehicule().getId(), selectedRow, 2);
        tableModel.setValueAt(r.getTotalAmount().toString(), selectedRow, 3);
        tableModel.setValueAt(r.getStartDate().toString(), selectedRow, 4);
        tableModel.setValueAt(r.getEndDate().toString(), selectedRow, 5);
        tableModel.setValueAt(r.getStatus(), selectedRow, 6);
    }

    private Reservation getSelectedReservation(int selectedRow) {

        return reservations.elementAt(selectedRow);
    }

    private void loadReservations(Vector<Reservation> rs) {

        reservations = rs;
        tableModel.setRowCount(0);
        for (Reservation r : rs) {
            addRow(r);
        }
    } 

    private void setButtons() {

        JButton returnButton = new JButton("Return");
        JButton paymentButton = new JButton("Payments");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(returnButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(cancelButton);

        returnButton.addActionListener(e -> returnVehicule(table.getSelectedRow()));
        paymentButton.addActionListener(e -> displayPayments(table.getSelectedRow()));
        cancelButton.addActionListener(e -> cancelReservation(table.getSelectedRow()));

        paymentButton.setEnabled(false);
        returnButton.setEnabled(false);
        cancelButton.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            returnButton.setEnabled(selectedRows.length == 1);
            paymentButton.setEnabled(selectedRows.length == 1);
            cancelButton.setEnabled(selectedRows.length == 1);
        });
    }

    private void setFilters() {

        setFiltersVariables();

        JToolBar filterToolbar = new JToolBar();

        JButton clientButton = new JButton("Filter by Client");
        clientButton.addActionListener(e -> showClients());
        filterToolbar.add(clientButton);

        JButton vehiculeButton = new JButton("Filter by Vehicule");
        vehiculeButton.addActionListener(e -> showVehicules());
        filterToolbar.add(vehiculeButton);

        JButton yearButton = new JButton("Filter by Total Price");
        yearButton.addActionListener(e -> showRangeDialog("Price Range", "Min Price", "Max Price"));
        filterToolbar.add(yearButton);

        JButton periodButton = new JButton("Filter by Period");
        periodButton.addActionListener(e -> showRangeDialog("Period", "Start Date(YYYY-MM-DD)", "End Date(YYYY-MM-DD)"));
        filterToolbar.add(periodButton);

        JButton dateButton = new JButton("Filter by Return Date");
        dateButton.addActionListener(e -> showRangeDialog("Return Range", "From Date (YYYY-MM-DD)", "To Date (YYYY-MM-DD)"));
        filterToolbar.add(dateButton);

        returnStateBox = new JComboBox<>(new String[]{"Filter by Return State", "All", "Good", "Flawed"});
        filterToolbar.add(returnStateBox);

        reservationStatusBox = new JComboBox<>(new String[]{"Filter by Reservation Status", "All", "Done", "Canceled", 
            "In Effect", "Currently Ongoing"});
        filterToolbar.add(reservationStatusBox);

        paymentStateBox = new JComboBox<>(new String[]{"Filter by Payment State","All","Paid in Full","Incomplete Payment"});
        filterToolbar.add(paymentStateBox);

        JButton resetButton = new JButton("Reset Filters");
        resetButton.addActionListener(e -> resetFilters());
        filterToolbar.add(resetButton);

        JButton applyButton = new JButton("Apply Filters");
        applyButton.addActionListener(e -> applyFilters());
        filterToolbar.add(applyButton);

        add(filterToolbar, BorderLayout.NORTH);
    }

    private void showRangeDialog(String title, String minLabel, String maxLabel) {

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), title, 
            JDialog.ModalityType.APPLICATION_MODAL);

        dialog.setSize(310, 150);
        dialog.setLayout(new GridLayout(3, 1, 5, 5));

        JTextField minField = new JTextField();
        JTextField maxField = new JTextField();
        dialog.add(form(minField, minLabel+":"));
        dialog.add(form(maxField, maxLabel+":"));

        JButton applyButton = new JButton("Save");
        applyButton.addActionListener(e -> {
            String minValue = minField.getText().trim();
            String maxValue = maxField.getText().trim();
            saveFilters(title, minValue, maxValue);
            dialog.dispose();
        });
        dialog.add(panel(applyButton));

        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void saveFilters(String title, String minValue, String maxValue) {

        int i=-1;
        switch (title) {
            case "Price Range":
                i=0;
                break;
            case "Period":
                i=1;
                break;
            case "Return Range":
                i=2;
                break;
            default:
        }

        filters.add(i, new Pair<>(minValue,maxValue));
    }

    private JPanel form(JTextField tf, String l) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,2));
        panel.add(new JLabel(l));
        panel.add(tf);
        return panel;
    }
    
    private JPanel panel(Component c) {

        JPanel panel = new JPanel();
        panel.add(c);
        return panel;
    }

    private void addRow(Reservation r) {

        tableModel.addRow(new Object[]{
            r.getId(),
            r.getClient().getId(),
            r.getVehicule().getId(),
            r.getTotalAmount(),
            r.getStartDate(),
            r.getEndDate(),
            r.getStatus()
        });
    }

    private void returnVehicule(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        Reservation selectedReservation = getSelectedReservation(selectedRow);
        ReturnDialog dialog = new ReturnDialog(SwingUtilities.getWindowAncestor(this), selectedReservation);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Return r = dialog.getReturnData();
            if (Return.add(r)) {
                JOptionPane.showMessageDialog(this, "Vehicule Returned Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }
    }

    private void displayPayments(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        Reservation selectedReservation = getSelectedReservation(selectedRow);
        PaymentDialog dialog = new PaymentDialog(SwingUtilities.getWindowAncestor(this), selectedReservation);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Payment r = dialog.getPaymentData();
            if (Payment.add(r)) {
                JOptionPane.showMessageDialog(this, "Vehicule Returned Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }
    }

    private void cancelReservation(int selectedRow) {

        int response = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to cancel this reservation?",
            "Confirm Cancelation",
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE
        );

        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        Reservation selectedReservation = getSelectedReservation(selectedRow);
        if (selectedReservation.cancel()) {
            JOptionPane.showMessageDialog(this, "Canceled Successfully!");
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, "Error while canceling!");
        }
    }

    private void showClients() {

        SelectClientDialog dialog = new SelectClientDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            client = dialog.getClientData();
        }
    }

    private void showVehicules() {

        SelectVehiculeDialog dialog = new SelectVehiculeDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            vehicule = dialog.getVehiculeData();
        }
    }
    
    private void applyFilters() {

        String returnState = (String) returnStateBox.getSelectedItem();
        String reservationStatus = (String) reservationStatusBox.getSelectedItem();
        String paymentState = (String) paymentStateBox.getSelectedItem();

        if (returnState.equals("Filter by Return State") || reservationStatus.equals("Filter by Reservation Status")) {
            JOptionPane.showMessageDialog(this, "Return State and Reservaiton Status must be selected!");
            return;
        }

        Vector<Reservation> filteredReservations = UserInterface.filterReservations(
            client,
            vehicule,
            returnState,
            reservationStatus,
            paymentState,
            filters
        );

        loadReservations(filteredReservations);
    }

    private void resetFilters() {

        setFiltersVariables();
        loadReservations(UserInterface.getAllReservations());
    }

    private void setFiltersVariables() {
        
        filters = new Vector<>(4);
        filters.add(0, new Pair<>());
        filters.add(1, new Pair<>());
        filters.add(2, new Pair<>());
        filters.add(3, new Pair<>());
        client = null;
        vehicule = null;
    }
}

class ReservationDialog extends JDialog {

    private JButton clientButton, vehiculeButton;
    private JTextField startDateField, endDateField;
    private Client client;
    private Vehicule vehicule;

    private boolean confirmed = false;

    public ReservationDialog(Window parent, Reservation reservation) {

        super(parent, "Reservation Details", ModalityType.APPLICATION_MODAL);
        initialize(reservation);
    }

    private void initialize(Reservation reservation) {

        setTitle(reservation == null ? "Add Reservation" : "Edit Reservation");
        setSize(400, 300);
        setLayout(new GridLayout(7, 2, 5, 5));
        setLocationRelativeTo(null);

        clientButton = new JButton("Choose Client");
        clientButton.addActionListener(e -> {
            SelectClientDialog dialog = new SelectClientDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                client = dialog.getClientData();
            }
        });

        if (reservation != null) clientButton.setEnabled(false);
        add(new JLabel("Client:"));
        add(clientButton);

        vehiculeButton = new JButton("Choose Vehicule");
        vehiculeButton.addActionListener(e -> {

            SelectVehiculeDialog dialog = new SelectVehiculeDialog(SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                vehicule = dialog.getVehiculeData();
            }
        });

        if (reservation != null) vehiculeButton.setEnabled(false);
        add(new JLabel("Client:"));
        add(vehiculeButton);

        startDateField = new JTextField(reservation == null ? "" : reservation.getStartDate().toString());
        add(new JLabel("Start Date (YYYY-MM-DD):"));
        add(startDateField);

        endDateField = new JTextField(reservation == null ? "" : reservation.getEndDate().toString());
        add(new JLabel("End Date (YYYY-MM-DD):"));
        add(endDateField);

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

    public Reservation getReservationData() {

        if (!confirmed) {
            return null;
        }

        LocalDate startDate = parseDate(startDateField.getText().trim());
        LocalDate endDate = parseDate(endDateField.getText().trim());

        return new Reservation(client, vehicule, startDate, endDate);
    }

    private LocalDate parseDate(String dateStr) {

        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

class ReturnDialog extends JDialog {

    private JTextField returnDateField, additionalFeesField;
    private JComboBox<String> returnStateBox, methodComboBox;
    private Reservation reservation;

    private boolean confirmed = false;

    public ReturnDialog(Window parent, Reservation reservation) {

        super(parent, "Return Details", ModalityType.APPLICATION_MODAL);
        this.reservation = reservation;
        initialize();
    }

    private void initialize() {

        Return returnData = UserInterface.getReservationReturn(reservation);

        setTitle(returnData == null ? "Return Vehicule" : "Edit Return");
        setSize(400, 250);
        setLayout(new GridLayout(5, 2, 5, 5));
        setLocationRelativeTo(null);

        returnDateField = new JTextField(returnData == null ? "" : returnData.getReturnDate());
        add(new JLabel("Return Date (YYYY-MM-DD):"));
        add(returnDateField);

        returnStateBox = new JComboBox<>(new String[]{"Select State", "All", "Good", "Flawed"});
        if (returnData != null) {
            returnStateBox.setSelectedItem(returnData.getReturnState());
        }
        add(new JLabel("Return State:"));
        add(returnStateBox);

        additionalFeesField = new JTextField(returnData == null ? "0.0" : returnData.getAdditionalFees().toString());
        add(new JLabel("Additional Fees:"));
        add(additionalFeesField);

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

    public Return getReturnData() {

        if (!confirmed) {
            return null;
        }

        String returnDate = parseDate(returnDateField.getText().trim());
        String returnState = (String) returnStateBox.getSelectedItem();
        returnState = returnState.equals("Select State") ? null : returnState;

        Double additionalFees;
        try {
            additionalFees = Double.parseDouble(additionalFeesField.getText().trim());
        } catch (NumberFormatException e) {
            additionalFees = null;
        }

        return new Return(reservation, returnDate, returnState, additionalFees);
    }

    private String parseDate(String dateStr) {

        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return dateStr;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}

class PaymentDialog extends JDialog {

    private JTextField amountField, dateField;
    private JComboBox<String> methodComboBox;
    private Reservation reservation;

    private boolean confirmed = false;

    public PaymentDialog(Window parent, Reservation reservation) {

        super(parent, "Payment Details", ModalityType.APPLICATION_MODAL);
        initialize();
        this.reservation = reservation;
    }

    private void initialize() {

        setTitle("Add Payment");
        setSize(400, 250);
        setLayout(new GridLayout(5, 2, 5, 5));
        setLocationRelativeTo(null);

        amountField = new JTextField("0.0");
        add(new JLabel("Amount:"));
        add(amountField);

        dateField = new JTextField("");
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(dateField);

        add(new JLabel("Method:"));
        methodComboBox = new JComboBox<>(new String[]{"Cash", "CreditCard", "DebitCard", "MobilePaymentApps", "BankTransfer"});
        add(methodComboBox);

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

    public Payment getPaymentData() {

        if (!confirmed) {
            return null;
        }

        String date = parseDate(dateField.getText().trim());
        String method = (String) methodComboBox.getSelectedItem();

        Double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
        } catch (NumberFormatException e) {
            amount = null;
        }

        return new Payment(reservation, amount, date, method);
    }

    private String parseDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return dateStr;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}

class SelectClientDialog extends JDialog {

    private ClientSelection clientSelection;
    private Client client;

    private boolean confirmed = false;

    public SelectClientDialog(Window parent) {

        super(parent, "Select Client", ModalityType.APPLICATION_MODAL);

        clientSelection = new ClientSelection();
        clientSelection.setSize(1005, 500);
        add(clientSelection);

        JButton selectButton = clientSelection.getSelectButton();
        JButton cancelButton = clientSelection.getCancelButton();

        selectButton.addActionListener(e -> {
            client = clientSelection.getSelectedClient(clientSelection.getTable().getSelectedRow());
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            dispose();
        });

        pack();
    }

    public Client getClientData() {

        if (!confirmed) {
            return null;
        }

        return client;
    }

    public boolean isConfirmed() {

        return confirmed;
    }
}

class SelectVehiculeDialog extends JDialog {

    private VehiculeSelection vehiculeSelection;
    private Vehicule vehicule;

    private boolean confirmed = false;

    public SelectVehiculeDialog(Window parent) {

        super(parent, "Select Vehicule", ModalityType.APPLICATION_MODAL);

        vehiculeSelection = new VehiculeSelection();
        vehiculeSelection.setSize(1005, 500);
        add(vehiculeSelection);

        JButton selectButton = vehiculeSelection.getSelectButton();
        JButton cancelButton = vehiculeSelection.getCancelButton();

        selectButton.addActionListener(e -> {
            vehicule = vehiculeSelection.getSelectedVehicule(vehiculeSelection.getTable().getSelectedRow());
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            dispose();
        });

        pack();
    }

    public Vehicule getVehiculeData() {

        if (!confirmed) {
            return null;
        }

        return vehicule;
    }

    public boolean isConfirmed() {

        return confirmed;
    }
}

class ClientSelection extends ClientPanel {

    private JButton selectButton, cancelButton;

    public ClientSelection() {

        buttonPanel.removeAll();
        buttonPanel.revalidate();
        buttonPanel.repaint();

        selectButton = new JButton("Select");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
    }

    public JButton getSelectButton() {

        return selectButton;
    }

    public JButton getCancelButton() {

        return cancelButton;
    }
}

class VehiculeSelection extends VehiculePanel {

    private JButton selectButton, cancelButton;

    public VehiculeSelection() {

        buttonPanel.removeAll();
        buttonPanel.revalidate();
        buttonPanel.repaint();

        selectButton = new JButton("Select");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
    }

    public JButton getSelectButton() {

        return selectButton;
    }

    public JButton getCancelButton() {

        return cancelButton;
    }
}
