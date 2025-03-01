package dashboard.panel.table;

import orm.Table;
import orm.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

public class UserPanel extends AbstractTablePanel {

    private Vector<User> users;

    public UserPanel() {

        super("Users", new String[]{"ID", "Surname", "Name", "Email", "Password", "Role"});

        loadTableData();
        setTopPanel();
    }

    @Override
    protected void loadTableData() {

        updateTable(User.search((User)null));
    }

    @Override
    protected void onAdd() {

        UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            if (User.add(dialog.getUserData())) {
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

        User user = getSelectedUser(selectedRow);
        UserDialog dialog = new UserDialog(SwingUtilities.getWindowAncestor(this), user);
        dialog.setVisible(true);

        if (dialog.isConfirmed() ) {
            if (UserInterface.editUser(user, dialog.getUserData())) {
                updateRow(selectedRow, user);
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

        User user = getSelectedUser(selectedRow);

        if (user.delete()) {
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Error in user deletion.");
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

        UserSearchProfileDialog dialog = new UserSearchProfileDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            User searchCriteria = dialog.getSearchCriteria();
            Vector<User> results = User.search(searchCriteria);
            if (results != null && !results.isEmpty()) {
                updateTable(results);
            } else if (results == null) {
                JOptionPane.showMessageDialog(this, "Error in user search.", "Search Result", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No users found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void performSearch(JTextField searchField) {

        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Vector<User> results = User.search(searchTerm);
        if (results != null && !results.isEmpty()) {
            updateTable(results);
        } else if (results == null) {
            JOptionPane.showMessageDialog(this, "Error in user search.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No users found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public User getSelectedUser(int row) {

        return users.elementAt(row);
    }


    private void updateTable(Vector<User> users) {

        this.users = users;
        tableModel.setRowCount(0);
        for (User user : users) {
            newRow(user);
        }
    }

    private void newRow(User client) {

        tableModel.addRow(new Object[]{
            client.getId(),
            client.getSurname(),
            client.getName(),
            client.getEmail(),
            client.getPassword(),
            client.getRole()
        });
    }

    private void updateRow(int row, User client) {

        tableModel.setValueAt(client.getId(), row, 0);
        tableModel.setValueAt(client.getSurname(), row, 1);
        tableModel.setValueAt(client.getName(), row, 2);
        tableModel.setValueAt(client.getEmail(), row, 3);
        tableModel.setValueAt(client.getPassword(), row, 4);
        tableModel.setValueAt(client.getRole(), row, 5);
    }

    private JPanel panel(Component c) {

        JPanel panel = new JPanel();
        panel.add(c);
        return panel;
    }
}


class UserDialog extends JDialog {

    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JTextField drivingLicenceField;
    private JTextField phoneNumberField;

    private boolean confirmed = false;

    public UserDialog(Window parent, User client) {

        super(parent, "User Details", ModalityType.APPLICATION_MODAL);
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

        inputPanel.add(new JLabel("Password:"));
        drivingLicenceField = new JTextField(client != null ? client.getPassword() : "");
        inputPanel.add(drivingLicenceField);

        inputPanel.add(new JLabel("Role:"));
        phoneNumberField = new JTextField(client != null ? client.getRole() : "");
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

    public User getUserData() {

        if (!confirmed) return null;

        return new User(
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

class UserSearchProfileDialog extends JDialog {

    private JTextField emailField;
    private JTextField phoneNumberField;
    private JTextField drivingLicenseField;
    private boolean confirmed = false;

    public UserSearchProfileDialog(Window parent) {

        super(parent, "Search Profile", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(3,1,1,1));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        emailField = new JTextField(15);
        formPanel.add(form(new JLabel("Email:"), emailField));

        phoneNumberField = new JTextField(15);
        formPanel.add(form(new JLabel("Password:"), phoneNumberField));

        drivingLicenseField = new JTextField(15);
        formPanel.add(form(new JLabel("Role:"), drivingLicenseField));

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

    public User getSearchCriteria() {

        User client = new User();
        client.setEmail(value(emailField.getText().trim()));
        client.setPassword(value(phoneNumberField.getText().trim()));
        client.setRole(value(drivingLicenseField.getText().trim()));
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
