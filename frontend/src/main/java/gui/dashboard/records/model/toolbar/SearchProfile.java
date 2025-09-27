package gui.dashboard.records.model.toolbar;

import java.awt.*;

import java.util.List;

import javax.swing.JTextField;

import java.util.*;

import gui.component.*;
import gui.component.Factory.Field;

import gui.util.Opts;

class SearchProfile extends MyDialog {

    Map<String,JTextField> fields = new HashMap<>();
    JTextField addField(String att, JTextField field) {
        fields.put(att, field);
        return field;
    }

    String[] atts;
    ToolBar toolBar;
    SearchProfile(ToolBar toolBar, String[] atts) {
        super("Search Profile");
        this.toolBar = toolBar;
        this.atts = atts;

        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyLabel(toolBar.model.parser.titleCase(atts[0])), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addField(atts[0], Factory.field(20, Field.TEXT)), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyLabel(toolBar.model.parser.titleCase(atts[1])), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addField(atts[1], Factory.field(20, Field.TEXT)), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyLabel(toolBar.model.parser.titleCase(atts[2])), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(addField(atts[2], Factory.field(20, Field.TEXT)), gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Save", e -> saveCriteria()), gbc);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    void saveCriteria() {
        System.out.println(fields.keySet());
        for (var att : atts) {
            System.out.println("Attempting to get: " + att);
            toolBar.addDiscreteCriteria(att, fields.get(att).getText());
        } dispose();
    }

}
