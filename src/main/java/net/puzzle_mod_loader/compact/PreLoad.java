package net.puzzle_mod_loader.compact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Preload a defined class before this class is loaded
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PreLoad {
    Class<?> value();
}
