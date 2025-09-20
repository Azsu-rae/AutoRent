package panel.table;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import orm.Table;
import orm.model.*;
import orm.util.Console;
import ui.component.MyPanel;

public class Reservations extends AbstractTable {

    private JTextField clientIdField, vehicleIdField, statusField;
    private JDateChooser startDateChooser, endDateChooser;

    public Reservations() {
        super("Reservation");
        // Form setup
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.add(new JLabel("Client ID:"));
        clientIdField = new JTextField();
        formPanel.add(clientIdField);
        formPanel.add(new JLabel("Vehicle ID:"));
        vehicleIdField = new JTextField();
        formPanel.add(vehicleIdField);
        formPanel.add(new JLabel("Start Date:"));
        startDateChooser = new JDateChooser();
        formPanel.add(startDateChooser);
        formPanel.add(new JLabel("End Date:"));
        endDateChooser = new JDateChooser();
        formPanel.add(endDateChooser);
        formPanel.add(new JLabel("Status:"));
        statusField = new JTextField("In Effect");
        formPanel.add(statusField);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Reservation");
        addButton.addActionListener(e -> addReservation());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteReservation());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
        loadReservations(); // Initial data load
    }

    private void loadReservations() {

        // Clear existing rows
        model.setRowCount(0);

        // Fetch data from ORM
        Vector<Table> reservations = Reservation.search();
        for (Table t : reservations) {
            Reservation r = (Reservation) t;
            Object[] row = {
                r.getId(),
                r.getClient() != null ? r.getClient().getId() : null,
                r.getVehicle() != null ? r.getVehicle().getId() : null,
                r.getTotalAmount(),
                r.getStartDate(),
                r.getEndDate(),
                r.getStatus()
            };
            model.addRow(row);
        }
    }

    private void addReservation() {
        try {
            // Validate inputs
            int clientId = Integer.parseInt(clientIdField.getText());
            int vehicleId = Integer.parseInt(vehicleIdField.getText());
            String startDate = startDateChooser.getDate() != null ?
                LocalDate.ofInstant(startDateChooser.getDate().toInstant(), java.time.ZoneId.systemDefault()).toString() : null;
            String endDate = endDateChooser.getDate() != null ?
                LocalDate.ofInstant(endDateChooser.getDate().toInstant(), java.time.ZoneId.systemDefault()).toString() : null;
            String status = statusField.getText();

            // Fetch Client and Vehicle
            Client client = (Client) Table.search(new Client().reflect.fields.set("id", clientId)).elementAt(0);
            Vehicle vehicle = (Vehicle) Table.search(new Vehicle().reflect.fields.set("id", vehicleId)).elementAt(0);

            // Create and save reservation
            Reservation reservation = new Reservation(client, vehicle, startDate, endDate);
            reservation.reflect.fields.set("status", status);
            if (reservation.add() > 0) {
                JOptionPane.showMessageDialog(this, "Reservation added successfully!");
                loadReservations(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add reservation (possible conflict).");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            Console.error(e);
        }
    }

    private void deleteReservation() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int id = (Integer) model.getValueAt(selectedRow, 0);
                Reservation reservation = (Reservation) Table.search(new Reservation().reflect.fields.set("id", id)).elementAt(0);
                if (reservation.delete() > 0) {
                    JOptionPane.showMessageDialog(this, "Reservation deleted successfully!");
                    loadReservations();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete reservation.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                Console.error(e);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation to delete.");
        }
    }
}
