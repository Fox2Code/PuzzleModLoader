package net.puzzle_mod_loader.launch.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * /!\\ THIS CLASS MUST STAY INSIDE "net.puzzle_mod_loader.launch." PACKAGE /!\\
 * MOVING THE CLASS WILL CAUSE THE CLASS TO NOT BE LOADED IN THE WRONG CONTEXT
 * AND CRASHING THE GAME OR RESULT IN THE EVENT MANAGER NOT WORKING
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
    EventPriority priority() default EventPriority.NORMAL;
    boolean ignoreCanceled() default false;
}
