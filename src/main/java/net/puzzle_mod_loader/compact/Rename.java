package net.puzzle_mod_loader.compact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rename the method at compile
 * (Allow having multiple method with same name and descriptor)
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Rename {
    String value();
}
