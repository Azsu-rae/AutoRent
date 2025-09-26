package gui.component;

import java.awt.*;
import javax.swing.*;

public class MyLabel extends JLabel {
    public MyLabel(String content) {
        super(content);
        if (!Opts.CUSTOM_THEME) return;
        setForeground(Color.WHITE);
        setFont(MyFont.LABEL); // Font size
    }
}
