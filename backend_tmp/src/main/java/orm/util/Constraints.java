package orm.util;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Constraints {

    String type();

    boolean nullable() default true; // Nothing can compensate for nullable() except primaryKey of course

    boolean bounded() default false;
    boolean lowerBound() default false;     // A lowerBound must be directly or indirectly followed by it's 
    boolean upperBound() default false;     // corresponding upperBound before having any other lowerBound
    boolean searchedText() default false;   // Whether to use the LIKE operator

    boolean primaryKey() default false;
    boolean foreignKey() default false;
}
