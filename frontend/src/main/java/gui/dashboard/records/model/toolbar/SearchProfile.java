package gui.dashboard.records.model.toolbar;

import java.awt.*;

import gui.component.*;
import gui.component.Factory.Field;

import gui.util.Opts;

class SearchProfile extends MyDialog {
    ToolBar toolBar;
    SearchProfile(ToolBar toolBar, String[] atts) {
        super("Search Profile");
        this.toolBar = toolBar;
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
        panel.add(Factory.field(Field.TEXT), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyLabel(toolBar.model.parser.titleCase(atts[1])), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(Factory.field(Field.TEXT), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyLabel(toolBar.model.parser.titleCase(atts[2])), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(Factory.field(Field.TEXT), gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new MyButton("Save", e -> dispose()), gbc);

        setContentPane(panel);
        setSize(200, 175);
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }
}
