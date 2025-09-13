package component;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.SwingConstants;

public class SidebarButton extends Button {

    public SidebarButton(String text) {

        super(text);
        setFocusPainted(false);     // no ugly focus border
        setBorderPainted(false);    // no border
        setFont(new Font("Segoe UI", Font.PLAIN, 16));
        setHorizontalAlignment(SwingConstants.LEFT); // text aligned left
        setMargin(new Insets(10, 20, 10, 10)); // padding (top, left, bottom, right)
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // hand on hover
    }
}
