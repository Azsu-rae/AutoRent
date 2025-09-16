package ui.component;

import java.awt.*;
import javax.swing.*;

public class MyLabel extends JLabel {

    public MyLabel(String content) {
        super(content);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 15)); // Font size
    }
}
