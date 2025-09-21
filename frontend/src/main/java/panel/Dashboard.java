package panel;

import java.awt.*;

import ui.component.*;

import util.Listener;
import util.Source;

public class Dashboard extends MyPanel implements Source, Listener {

    CardLayout cardLayout = new CardLayout();
    MyPanel panels = new MyPanel(cardLayout);

    Listener listener;
    public Dashboard(Listener listener) {
        this.listener = listener;

        panels.add(new Home(), "Home");
        panels.add(new Models(), "Models");

        setLayout(new BorderLayout());
        add(new Sidebar(this), BorderLayout.WEST);
        add(panels, BorderLayout.CENTER);
    }

    @Override
    public void notifyListener(Event event) {
        listener.onEvent(event);
    }

    @Override
    public void onEvent(Event event) {
        switch (event) {
            case LOG_OUT:
                notifyListener(event);
                break;
            case HOME:
                cardLayout.show(panels, "Home");
                break;
            case MODELS:
                cardLayout.show(panels, "Models");
                break;
            default:
                break;
        }
    }
}
