package orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Constraints {

    String INTEGER = "INT";

    String type();

    boolean nullable() default true; // Nothing can compensate for nullable() except primaryKey of course

    boolean bounded() default false;

    boolean lowerBound() default false;

    boolean upperBound() default false;

    String boundedPair() default "";

    boolean searchedText() default false; // use the LIKE operator

    boolean enumerated() default false;

    boolean unique() default false;
}
