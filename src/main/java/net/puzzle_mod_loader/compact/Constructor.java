package net.puzzle_mod_loader.compact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Build a non public class
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Constructor {
    String value() default "";
}
