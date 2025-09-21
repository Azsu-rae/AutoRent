package ui.component;

import java.awt.*;
import javax.swing.*;

import ui.style.*;
import util.Opts;

public class MyLabel extends JLabel {
    public MyLabel(String content) {
        super(content);
        if (Opts.DEFAULT_THEME) return;
        setForeground(Color.WHITE);
        setFont(MyFont.LABEL); // Font size
    }
}
