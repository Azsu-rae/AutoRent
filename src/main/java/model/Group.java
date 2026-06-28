package model;

import orm.Table;

import orm.Constraints;

public class Group extends Table {

    static {
        registerModel(Group.class);
    }

    @Constraints(type = "INTEGER")
    private Integer num;
    @Constraints(type = "INTEGER", foreignKey = true)
    private Integer teachingAssistant;
}
