package fr.jamailun.reignofcubes2.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TagProperty {

    String name();

    double defaultValue() default 0;

    double minValue() default Double.MIN_VALUE;
    double maxValue() default Double.MAX_VALUE;

}
