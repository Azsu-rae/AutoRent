package gui.dashboard.records.model.toolbar;

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
