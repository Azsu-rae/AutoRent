package model;

import orm.Constraints;
import orm.Constraints.Type;
import orm.Table;

public class Student extends Table {

    static {
        registerModel(Student.class);
    }

    @Constraints(type = Type.TEXT, nullable = false)
    private String name;
    @Constraints(type = Type.TEXT, nullable = false)
    private String surname;

    @Constraints(type = Type.INTEGER, nullable = false)
    private Integer matricule;
    @Constraints(type = Type.INTEGER, nullable = false)
    private String academicLevel;

    @Constraints(type = Type.FOREIGN_KEY)
    private Integer specialty;
    @Constraints(type = Type.FOREIGN_KEY)
    private Integer foreignKey;
}
