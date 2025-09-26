package gui.dashboard.records.model;

public class Model extends MyPanel {

    String ORMModelName;
    Reflection reflect;
    Parser parser;

    ToolBar toolBar;
    Table table;
    Form form;

    public Model(String ORMModelName) {
        this.ORMModelName = ORMModelName;

        reflect = new Reflection(ORMModelName);
        parser = new Parser();
        toolBar = new ToolBar(this);
        table = new Table();
        form = new Form();

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(form, BorderLayout.SOUTH);
    }



}

