package gui.dashboard.records.model.toolbar;

import javax.swing.*;
import java.awt.*;

import gui.component.*;
import gui.util.Opts;

import orm.Table.Range;
import orm.util.Console;

class RangeSelection extends MyDialog {

    JTextField lower, upper;
    String att;

    ToolBar toolBar;
    RangeSelection(ToolBar toolBar, String title, String att) {
        super(title);
        this.att = att;
        this.toolBar = toolBar;

        String lowerBound = null, upperBound = null;
        var constraint = toolBar.model.reflect.fields.constraintsOf(att);
        if (constraint.lowerBound()) {
            lowerBound = toolBar.model.parser.titleCase(att) + ":";
            upperBound = toolBar.model.parser.titleCase(constraint.boundedPair()) + ":";
        } else if (constraint.bounded()) {
            lowerBound = toolBar.model.parser.getMin(att);
            upperBound = toolBar.model.parser.getMax(att);
        }

        var panel = new MyPanel();
        panel.setLayout(new GridBagLayout());
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new MyLabel(lowerBound), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        lower = Factory.field(15, Factory.Field.TEXT);
        panel.add(lower, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new MyLabel(upperBound), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        upper = Factory.field(15, Factory.Field.TEXT);
        panel.add(upper, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        panel.add(new MyButton("Save", e -> saveCriteria()), gbc);

        setContentPane(panel);
        setSize(200, 175);
        setLocationRelativeTo(Opts.MAIN_FRAME);
        setVisible(true);
    }

    public void saveCriteria() {

        Object parsedLower = toolBar.model.parser.parse(att, lower);
        Object parsedUpper = toolBar.model.parser.parse(att, upper);
        var range = new Range(att, parsedLower, parsedUpper);
        if (range.isValidCriteriaFor(toolBar.model.reflect)) {
            toolBar.boundedValues.add(range);
            dispose();
        } else {
            dispose();
            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
            Console.print("Invalid Range: %s", range);
        }
    }
}
