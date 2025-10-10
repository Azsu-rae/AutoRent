//package gui.dashboard.record;
//
//import javax.swing.*;
//import java.awt.*;
//
//import gui.component.*;
//import gui.Opts;
//
//import orm.Table.Range;
//import orm.util.Console;
//
//class RangeSelection extends MyDialog {
//
//    JTextField[] fields = new JTextField[2];
//    String[] labels = new String[2];
//    String att;
//
//    ToolBar toolBar;
//    RangeSelection(ToolBar toolBar, String title, String att) {
//        super(title);
//        this.att = att;
//        this.toolBar = toolBar;
//
//        var constraint = toolBar.model.reflect.fields.constraintsOf(att);
//        if (constraint.lowerBound()) {
//            labels[0] = toolBar.model.parser.titleCase(att) + ":";
//            labels[1] = toolBar.model.parser.titleCase(constraint.boundedPair()) + ":";
//        } else if (constraint.bounded()) {
//            labels[0] = toolBar.model.parser.getMin(att);
//            labels[1] = toolBar.model.parser.getMax(att);
//        }
//
//        var fieldsPanel = Factory.createForm(labels, fields);
//
//        var panel = new MyPanel();
//        panel.setLayout(new GridBagLayout());
//        var gbc = Factory.initFormGBC();
//
//        gbc.gridx = 0; gbc.gridy = 0;
//        panel.add(fieldsPanel);
//
//        gbc.anchor = GridBagConstraints.CENTER;
//        gbc.fill = GridBagConstraints.NONE;
//        gbc.gridx = 0; gbc.gridy = 1;
//        gbc.weightx = 0;
//        panel.add(new MyButton("Save", e -> saveCriteria()), gbc);
//
//        setContentPane(panel);
//        pack();
//        setLocationRelativeTo(Opts.MAIN_FRAME);
//        setVisible(true);
//    }
//
//    public void saveCriteria() {
//
//        Object parsedLower = toolBar.model.parser.parse(att, fields[0]);
//        Object parsedUpper = toolBar.model.parser.parse(att, fields[1]);
//        var range = new Range(att, parsedLower, parsedUpper);
//        if (range.isValidCriteriaFor(toolBar.model.reflect)) {
//            toolBar.boundedValues.add(range);
//            dispose();
//        } else {
//            dispose();
//            JOptionPane.showMessageDialog(this, "Please Enter a Valid Input!");
//            Console.print("Invalid Range: %s", range);
//        }
//    }
//}
