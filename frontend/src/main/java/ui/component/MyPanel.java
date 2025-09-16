package ui.component;

import java.awt.Color;

import javax.swing.JPanel;

public class MyPanel extends JPanel {

    public MyPanel() {
        setBackground(new Color(58, 58, 58)); 
    }

    public MyPanel(Color color) {
        setBackground(color); 
    }
}
