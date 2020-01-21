package net.puzzle_mod_loader.launch.event;

/**
 * /!\\ THIS CLASS MUST STAY INSIDE "net.puzzle_mod_loader.launch." PACKAGE /!\\
 * MOVING THE CLASS WILL CAUSE THE CLASS TO NOT BE LOADED IN THE RIGHT CONTEXT
 * AND CRASHING THE GAME OR RESULT IN THE EVENT MANAGER NOT WORKING
 */
public enum EventPriority {
    ASYNC,
    MONITOR,
    HIGHEST,
    HIGH,
    NORMAL,
    LOW,
    LOWEST
}
