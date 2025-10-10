package gui.dashboard;

import java.awt.*;

import gui.contract.*;
import gui.component.MyPanel;

public class Dashboard extends MyPanel implements Listener {

    CardLayout cardLayout = new CardLayout();
    MyPanel panels = new MyPanel(cardLayout);

    Listener listener;
    public Dashboard(Listener listener) {
        this.listener = listener;

        panels.add(new Home(), "Home");
        panels.add(new Records(), "Records");

        setLayout(new BorderLayout());
        add(new Sidebar(this), BorderLayout.WEST);
        add(panels, BorderLayout.CENTER);
    }

    @Override
    public void onEvent(Event event) {
        switch (event) {
            case LOG_OUT:
                listener.onEvent(event);
                break;
            case HOME:
                cardLayout.show(panels, "Home");
                break;
            case MODELS:
                cardLayout.show(panels, "Records");
                break;
            default:
                break;
        }
    }
}
