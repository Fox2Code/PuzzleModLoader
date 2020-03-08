package net.puzzle_mod_loader.compact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to call super method/field
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Super {
}
