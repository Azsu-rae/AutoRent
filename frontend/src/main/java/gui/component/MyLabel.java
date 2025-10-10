package gui.component;

import java.awt.Color;

import javax.swing.JLabel;

import gui.style.MyFont;
import gui.Opts;

public class MyLabel extends JLabel {
    public MyLabel(String content) {
        super(content);
        if (!Opts.CUSTOM_THEME) return;
        setForeground(Color.WHITE);
        setFont(MyFont.LABEL); // Font size
    }
}
