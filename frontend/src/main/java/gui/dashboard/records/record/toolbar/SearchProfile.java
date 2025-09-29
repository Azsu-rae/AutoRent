package gui.dashboard.records.record.toolbar;

import java.awt.*;

import java.util.List;

import javax.swing.JTextField;

import java.util.*;

import gui.component.*;
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

        var labels = List.of(atts).stream().map(toolBar.model.parser::titleCase).toArray(String[]::new);
        var placeholder = new JTextField[atts.length];
        var form = Factory.createForm(labels, placeholder);

        var gbc = Factory.initFormGBC();
        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(form);

        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Save", e -> saveCriteria()), gbc);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    void saveCriteria() {
        for (var att : atts) {
            toolBar.addDiscreteCriteria(att, fields.get(att).getText());
        } dispose();
    }

}
