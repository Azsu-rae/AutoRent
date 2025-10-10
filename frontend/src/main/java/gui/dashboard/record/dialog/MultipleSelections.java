//package gui.dashboard.record;
//
//import javax.swing.*;
//import java.awt.*;
//
//import gui.component.*;
//
//import gui.Opts;
//
//import static orm.Reflection.getModelInstance;
//
//class MultipleSelections extends MyDialog {
//
//    JCheckBox[] checkBoxes;
//    String att;
//
//    ToolBar toolBar;
//    public MultipleSelections(ToolBar toolBar, String title, String att) {
//        super(title);
//        this.att = att;
//        this.toolBar = toolBar;
//
//        var checkboxPanel = new MyPanel();
//        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
//
//        var values = getModelInstance(toolBar.model.ORMModelName).getAttributeValues(att);
//        checkBoxes = new JCheckBox[values.size()];
//
//        int i=0;
//        for (var value : values) {
//            checkBoxes[i] = new JCheckBox(value);
//            checkboxPanel.add(checkBoxes[i]);
//            i++;
//        }
//
//        var panel = new MyPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//        var scrollPane = new JScrollPane(checkboxPanel);
//        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
//        panel.add(scrollPane);
//        panel.add(new MyButton("Save", e -> saveCriteria(), Component.CENTER_ALIGNMENT));
//
//        setContentPane(panel);
//        setSize(200, 200);
//        setLocationRelativeTo(Opts.MAIN_FRAME);
//        setVisible(true);
//    }
//
//    public void saveCriteria() {
//        for (var checkBox : checkBoxes) {
//            if (checkBox.isSelected()) {
//                toolBar.addDiscreteCriteria(att, checkBox.getText());
//            }
//        } dispose();
//    }
//}
