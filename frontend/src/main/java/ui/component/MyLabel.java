package ui.component;

import java.awt.*;
import javax.swing.*;

import ui.style.*;

public class MyLabel extends JLabel {
    public MyLabel(String content) {
        super(content);
        setForeground(Color.WHITE);
        setFont(MyFont.LABEL); // Font size
    }
}
