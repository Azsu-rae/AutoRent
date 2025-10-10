package gui.action;

import java.util.Vector;
import orm.Table;

public interface FilterAction {
    void onFilter(Vector<Table> tuples);
}
