//package gui.dashboard.record;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//import gui.component.*;
//import orm.Table;
//
//import static orm.Reflection.getModelInstance;
//
//class Editor extends MyPanel {
//
//    Map<String,JTextField> fieldByAtt = new HashMap<>();
//    String[] labels;
//
//    List<MyButton> btns = new ArrayList<>();
//    JTextField[] fields;
//
//    Record record;
//    Editor(Record record) {
////        this.record = record;
////
////        var modifiables = record.reflect.fields.modifiable();
////        fields = new JTextField[modifiables.size()];
////        labels = Parser.titleCaseNames(modifiables.toArray(String[]::new));
////        var fieldsPanel = Factory.createForm(labels, fields);
////
////        int i=0;
////        for (var att : modifiables) {
////            fieldByAtt.put(att, fields[i++]);
////        }
////
////        var buttonPanel = new MyPanel();
////        btns.add(new MyButton(buttonPanel, "Add", e -> record.recordGrid.onAdd(), true));
////        btns.add(new MyButton(buttonPanel, "Edit", e -> record.recordGrid.onEdit(), false));
////        btns.add(new MyButton(buttonPanel, "Delete", e -> record.recordGrid.onDelete(), false));
////        btns.add(new MyButton(buttonPanel, "Clear", e -> {
////            for (var field : fields) {
////                field.setText("");
////            }
////        }, true));
////
////        setLayout(new BorderLayout());
////        add(fieldsPanel, BorderLayout.CENTER);
////        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    Table parseFields() {
////        var tuple = getModelInstance(record.ORMModelName);
////        for (var field : fieldByAtt.entrySet()) {
////            Object value = record.parser.parse(field.getKey(), field.getValue());
////            tuple.reflect.fields.callSetter(field.getKey(), value);
////        } return tuple;
//        return null;
//    }
//}
