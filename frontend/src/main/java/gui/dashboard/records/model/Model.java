package panel.model;

import java.awt.*;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.*;
import java.util.List;
import java.util.function.Function;

import java.time.LocalDate;

import ui.Factory;
import ui.component.*;
import util.Opts;
import util.ToClear;
import orm.Table.Range;
import orm.util.*;

import ui.Factory.Field;

import static orm.util.Reflection.getModelInstance;

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

