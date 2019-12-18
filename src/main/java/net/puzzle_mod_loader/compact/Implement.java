package net.puzzle_mod_loader.compact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Implement a non public interface
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Implement {
    String value();
}
