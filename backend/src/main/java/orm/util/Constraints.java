package orm.util;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Constraints {

    String type();

    boolean nullable() default true; // Nothing can compensate for nullable() except primaryKey of course

    boolean bounded() default false;
    boolean lowerBound() default false;
    boolean upperBound() default false;
    String boundedPair() default "";

    boolean searchedText() default false;   // use the LIKE operator
    boolean enumerated() default false;
    boolean unique() default false;

    boolean primaryKey() default false;
    boolean foreignKey() default false;
}
