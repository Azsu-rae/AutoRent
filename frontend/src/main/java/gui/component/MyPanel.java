package gui.component;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;

import gui.style.MyColor;
import gui.Opts;

public class MyPanel extends JPanel {

    public MyPanel(Component... components) {
        this();
        for (var c : components) {
            add(c);
        }
    }

    public MyPanel(LayoutManager layoutManager) {
        this();
        setLayout(layoutManager);
    }

    public MyPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Opts.clearEvent();
            }
        });
        if (!Opts.CUSTOM_THEME) return;
        setBackground(MyColor.PANEL);
//        setOpaque(false);
    }
}
