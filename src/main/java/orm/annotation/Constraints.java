package orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Constraints {

    // DDL

    String INT = "INTEGER";
    String TEXT = "TEXT";

    String type();

    boolean foreignKey() default false;

    boolean primaryKey() default false;

    boolean nullable() default true; // Nothing can compensate for nullable() except primaryKey of course

    // DML

    boolean bounded() default false;

    boolean lowerBound() default false;

    boolean upperBound() default false;

    String boundedPair() default "";

    boolean searchedText() default false; // use the LIKE operator

    boolean enumerated() default false;

    boolean unique() default false;
}
