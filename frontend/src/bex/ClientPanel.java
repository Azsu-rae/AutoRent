package bex;

import aex.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

public class ClientPanel extends AbstractTablePanel {

    private Vector<Client> clients;

    public ClientPanel() {

        super("Clients", new String[]{"ID", "Surname", "Name", "Email", "Phone Number", "Driving Licence"});

        loadTableData();
        setTopPanel();
    }

    @Override
    protected void loadTableData() {

        updateTable(Client.search((Client)null));
    }

    @Override
    protected void onAdd() {

        ClientDialog dialog = new ClientDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (Client.add(dialog.getClientData())) {
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }
    }

    @Override
    protected void onEdit(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = getSelectedClient(selectedRow);
        ClientDialog dialog = new ClientDialog(SwingUtilities.getWindowAncestor(this), client);
        dialog.setVisible(true);

        if (dialog.isConfirmed() ) {
            if (UserInterface.editClient(client, dialog.getClientData())) {
                updateRow(selectedRow, client);
            } else {
                JOptionPane.showMessageDialog(this, "All fields cannot be empty and must have a valid format.");
            }
        }     
    }

    @Override
    protected void onDelete(int selectedRow) {

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Selection Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = getSelectedClient(selectedRow);
        Vector<Reservation> rs = UserInterface.getClientReservations(client);
        if (rs.size() > 0) {

            int response = JOptionPane.showConfirmDialog(
                this,
                "This client has ongoing reservations, do you still want to proceed?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (response != JOptionPane.YES_OPTION) {
                return;
            } 
        }

        if (UserInterface.deleteClient(client)) {
            JOptionPane.showMessageDialog(this, "Client deleted successfully.");
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Error in client deletion.");
        }
    }


    private void setTopPanel() {

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(new JLabel("Search by Name:"));

        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        JButton searchProfileButton = new JButton("Search Profile");
        searchPanel.add(searchProfileButton);

        JButton resetButton = new JButton("Reset");

        resetButton.addActionListener(e -> loadTableData());
        searchButton.addActionListener(e -> performSearch(searchField));
        searchProfileButton.addActionListener(e -> performProfileSearch());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(panel(resetButton), BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void performProfileSearch() {

        SearchProfileDialog dialog = new SearchProfileDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Client searchCriteria = dialog.getSearchCriteria();
            Vector<Client> results = Client.search(searchCriteria);
            if (results != null && !results.isEmpty()) {
                updateTable(results);
            } else if (results == null) {
                JOptionPane.showMessageDialog(this, "Error in client search.", "Search Result", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No clients found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void performSearch(JTextField searchField) {

        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Vector<Client> results = Client.search(searchTerm);
        if (results != null && !results.isEmpty()) {
            updateTable(results);
        } else if (results == null) {
            JOptionPane.showMessageDialog(this, "Error in client search.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No clients found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Client getSelectedClient(int row) {

        return clients.elementAt(row);
    }


    private void updateTable(Vector<Client> clients) {

        this.clients = clients;
        tableModel.setRowCount(0);
        for (Client client : clients) {
            newRow(client);
        }
    }

    private void newRow(Client client) {

        tableModel.addRow(new Object[]{
            client.getId(),
            client.getSurname(),
            client.getName(),
            client.getEmail(),
            client.getPhoneNumber(),
            client.getDrivingLicence()
        });
    }

    private void updateRow(int row, Client client) {

        tableModel.setValueAt(client.getId(), row, 0);
        tableModel.setValueAt(client.getSurname(), row, 1);
        tableModel.setValueAt(client.getName(), row, 2);
        tableModel.setValueAt(client.getEmail(), row, 3);
        tableModel.setValueAt(client.getPhoneNumber(), row, 4);
        tableModel.setValueAt(client.getDrivingLicence(), row, 5);
    }

    private JPanel panel(Component c) {

        JPanel panel = new JPanel();
        panel.add(c);
        return panel;
    }
}


class ClientDialog extends JDialog {

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField drivingLicenceField;
    private JTextField phoneNumberField;
    private boolean confirmed;

    public ClientDialog(Window parent, Client client) {

        super(parent, "Client Details", ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        // Input fields panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(client != null ? client.getName() : "");
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Surname:"));
        surnameField = new JTextField(client != null ? client.getSurname() : "");
        inputPanel.add(surnameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField(client != null ? client.getEmail() : "");
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Driving Licence:"));
        drivingLicenceField = new JTextField(client != null ? client.getDrivingLicence() : "");
        inputPanel.add(drivingLicenceField);

        inputPanel.add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField(client != null ? client.getPhoneNumber() : "");
        inputPanel.add(phoneNumberField);

        add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        confirmButton.addActionListener(e -> {
                if (validateInput()) {
                    confirmed = true;
                    dispose();
                }
        });

        cancelButton.addActionListener(e -> {
                confirmed = false;
                dispose();
        });
    }

    public boolean isConfirmed() {

        return confirmed;
    }

    public Client getClientData() {

        if (!confirmed) return null;

        return new Client(
                nameField.getText().trim(),
                surnameField.getText().trim(),
                emailField.getText().trim(),
                phoneNumberField.getText().trim(),
                drivingLicenceField.getText().trim()
        );
    }

    private boolean validateInput() {

        if (nameField.getText().trim().isEmpty() ||
            surnameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            drivingLicenceField.getText().trim().isEmpty() ||
            phoneNumberField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}

class SearchProfileDialog extends JDialog {

    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField drivingLicenseField;
    private boolean confirmed = false;

    public SearchProfileDialog(Window parent) {

        super(parent, "Search Profile", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(3,1,1,1));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        emailField = new JTextField(15);
        formPanel.add(form(new JLabel("Email:"), emailField));

        phoneNumberField = new JTextField(15);
        formPanel.add(form(new JLabel("Phone Number:"), phoneNumberField));

        drivingLicenseField = new JTextField(15);
        formPanel.add(form(new JLabel("Driving License:"), drivingLicenseField));

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> {
            if (validSearchCriteria()) {
                confirmed = true;
                dispose();
            } else {
                confirmed = false;
                JOptionPane.showMessageDialog(this, "At least one field must be filled.", "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        pack();
    }

    public boolean isConfirmed() {

        return confirmed;
    }

    public Client getSearchCriteria() {

        Client client = new Client();
        client.setEmail(value(emailField.getText().trim()));
        client.setPhoneNumber(value(phoneNumberField.getText().trim()));
        client.setDrivingLicence(value(drivingLicenseField.getText().trim()));
        return client;
    }

    private boolean validSearchCriteria() {

        return !emailField.getText().trim().isEmpty() || !phoneNumberField.getText().trim().isEmpty() || 
            !drivingLicenseField.getText().trim().isEmpty();
    }

    private String value(String s) {

        return s.equals("") ? null : s;
    }

    private JPanel panel(Component c) {

        JPanel panel = new JPanel();
        panel.add(c);
        return panel;
    }

    private JPanel form(Component c1, Component c2) {

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(1,2,5,5));
        form.add(c1);
        form.add(c2);
        return panel(form);
    }
}
