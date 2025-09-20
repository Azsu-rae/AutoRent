package ui.component;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import ui.style.MyColor;

public class MyPanel extends JPanel {

    public MyPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    public MyPanel() {
        setBackground(MyColor.PANEL);
//        setOpaque(false);
    }
}
