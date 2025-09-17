package ui.component;

import java.awt.Color;

import javax.swing.JPanel;

public class MyPanel extends JPanel {

    public MyPanel() {
        setBackground(new Color(25, 25, 25));
    }

    public MyPanel(Color color) {
        setBackground(color); 
    }
}
