//package gui.shared;
//
//import gui.component.MyDialog;
//import gui.component.MyPanel;
//import gui.contract.Listener;
//
//import java.awt.*;
//
//public class ForeignPicker extends MyDialog implements Listener {
//
//    public ForeignPicker(Listener listener, String title, String ORMModelName) {
//        super(title);
//        var panel = new MyPanel();
//        panel.setLayout(new BorderLayout());
//        panel.add(new Grid(listener, ORMModelName));
//        setContentPane(panel);
//        setVisible(true);
//    }
//
//    @Override
//    public void onEvent(Event e) {
//        // TODO Auto-generated method stub
//        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
//    }
//}
