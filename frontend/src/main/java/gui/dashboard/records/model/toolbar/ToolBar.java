package panel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.*;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import orm.Table.Range;
import orm.util.Constraints;
import panel.model.ToolBar.MultipleSelections;
import panel.model.ToolBar.RangeSelection;
import panel.model.ToolBar.SearchProfile;
import ui.Factory;
import ui.Factory.Field;
import ui.component.MyButton;
import ui.component.MyDialog;
import ui.component.MyLabel;
import ui.component.MyPanel;
import util.Opts;

class ToolBar extends JToolBar {

    Map<String,List<String>> discreteValues = new HashMap<>();
    Map<String,List<Range>> boundedValues = new HashMap<>();
    <T> void addCriteria(Map<String,List<T>> map, String att, T value) {
        map.computeIfAbsent(att, k -> new ArrayList<>()).add(value);
    }

    Model model;
    ToolBar(Model model) {
        this.model = model;

        var searchedTexts = model.reflect.fields.haveConstraint(Constraints::searchedText);
        if (searchedTexts.size() > 0) {
            add(new SearchBar(searchedTexts.toArray(String[]::new)));
        }

        var unique = model.reflect.fields.haveConstraint(Constraints::unique);
        if (unique.size() > 0) {
            add(new MyButton("Search Profile", e -> new SearchProfile(unique.toArray(String[]::new))));
        }

        for (var enumerated : model.reflect.fields.haveConstraint(Constraints::enumerated)) {
            var title =  model.parser.formatName(enumerated);
            add(new MyButton(this, title, e -> new MultipleSelections(title, enumerated)));
        }

        for (var bounded : model.reflect.fields.haveConstraint(c -> c.lowerBound() || c.bounded())) {
            var title =  model.parser.formatName(bounded);
            add(new MyButton(this, title, e -> new RangeSelection(title, bounded)));
        }

        add(Box.createHorizontalGlue());
        add(new MyButton("Apply", e -> {
            System.out.println(discreteValues.values());
            System.out.println(boundedValues.values());
        }));
    }

    class MultipleSelections extends MyDialog {

        JCheckBox[] checkBoxes;
        String att;

        public MultipleSelections(String title, String att) {
            super(title);
            this.att = att;

            var checkboxPanel = new MyPanel();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

            var values = getModelInstance(model.ORMModelName).getAttributeValues(att);
            checkBoxes = new JCheckBox[values.size()];

            int i=0;
            for (var value : values) {
                checkBoxes[i] = new JCheckBox(value);
                checkboxPanel.add(checkBoxes[i]);
                i++;
            }

            var panel = new MyPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            var scrollPane = new JScrollPane(checkboxPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            panel.add(scrollPane);
            panel.add(new MyButton("Save", e -> saveCriteria(), Component.CENTER_ALIGNMENT));

            setContentPane(panel);
            setSize(200, 200);
            setLocationRelativeTo(Opts.MAIN_FRAME);
            setVisible(true);
        }

        public void saveCriteria() {

            for (var checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    discreteValues.computeIfAbsent(att, k -> new ArrayList<>()).add(checkBox.getText());
                }
            }

            dispose();
        }
    }

    class RangeSelection extends MyDialog {

        JTextField lower, upper;
        String att;

        RangeSelection(String title, String att) {
            super(title);
            this.att = att;

            String lowerBound = null, upperBound = null;
            var constraint = model.reflect.fields.constraintsOf(att);
            if (constraint.lowerBound()) {
                lowerBound = model.parser.titleCase(att) + ":";
                upperBound = model.parser.titleCase(constraint.boundedPair()) + ":";
            } else if (constraint.bounded()) {
                lowerBound = model.parser.getMin(att);
                upperBound = model.parser.getMax(att);
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

            Object parsedLower = model.parser.parse(att, lower);
            Object parsedUpper = model.parser.parse(att, upper);
            var range = new Range(att, parsedLower, parsedUpper);
            if (range.isValidCriteriaFor(model.reflect)) {
                addCriteria(boundedValues, att, range);
                dispose();
            } else {
                dispose();
                JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
            }
        }
    }

    class SearchBar extends MyPanel {
        SearchBar(String[] atts) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new MyLabel("Search" + model.parser.denominator(atts)));
            var searchField = Factory.field(20, Field.TEXT);
            add(searchField);
            add(new MyButton("Search", e -> {
                for (var att : atts) {
                    addCriteria(discreteValues, att, searchField.getText());
                }
            }));
        }
    }

    class SearchProfile extends MyDialog {
        SearchProfile(String[] atts) {
            super("Search Profile");
            var panel = new MyPanel();
            panel.setLayout(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new MyLabel(model.parser.titleCase(atts[0])), gbc);

            gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(Factory.field(Field.TEXT), gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new MyLabel(model.parser.titleCase(atts[1])), gbc);

            gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(Factory.field(Field.TEXT), gbc);

            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new MyLabel(model.parser.titleCase(atts[2])), gbc);

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
}
