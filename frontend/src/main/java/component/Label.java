package component;

import java.awt.*;
import javax.swing.*;

public class Label extends JLabel {
    public Label(String content) {
        super(content);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 15)); // Font size
    }
}
