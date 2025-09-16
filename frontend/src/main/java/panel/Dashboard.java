package panel;

import javax.swing.*;
import java.awt.BorderLayout;

import ui.component.*;

import util.Listener;
import util.Source;

public class Dashboard extends MyPanel implements Source {

    public Dashboard(Listener listener) {

        setLayout(new BorderLayout());
        add(new Sidebar(), BorderLayout.WEST);
    }

    @Override
    public void notifyListener(Listener listener) {
        // TODO Auto-generated method stub
    }
}
