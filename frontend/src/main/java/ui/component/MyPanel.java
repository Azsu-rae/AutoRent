package ui.component;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import ui.style.MyColor;
import util.Opts;

public class MyPanel extends JPanel {

    public MyPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    public MyPanel() {
        if (Opts.DEFAULT_THEME) return;
        setBackground(MyColor.PANEL);
//        setOpaque(false);
    }
}
