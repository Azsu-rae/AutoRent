package ui.component;

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ui.style.MyColor;
import util.Opts;

public class MyPanel extends JPanel {

    public MyPanel(Component... components) {
        for (var c : components) {
            add(c);
        }
    }

    public MyPanel(LayoutManager layoutManager) {
        this();
        setLayout(layoutManager);
    }

    public MyPanel() {
        if (Opts.DEFAULT_THEME) return;
        setBackground(MyColor.PANEL);
//        setOpaque(false);
    }
}
