package gui.dashboard.records.record;

import java.awt.BorderLayout;

import gui.util.Parser;
import gui.component.MyPanel;
import gui.dashboard.records.record.toolbar.ToolBar;

import orm.util.Reflection;

public class Record extends MyPanel {

    public Reflection reflect;
    public Parser parser;

    public RecordGrid recordGrid;
    RecordEditor recordEditor;
    ToolBar toolBar;

    public String ORMModelName;
    public Record(String ORMModelName) {
        this.ORMModelName = ORMModelName;

        reflect = new Reflection(ORMModelName);
        parser = new Parser(reflect);

        recordEditor = new RecordEditor(this);
        recordGrid = new RecordGrid(this);
        toolBar = new ToolBar(this);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(recordGrid, BorderLayout.CENTER);
        add(recordEditor, BorderLayout.SOUTH);
    }
}
