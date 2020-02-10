package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.event.tests.TestEvent;
import net.puzzle_mod_loader.launch.event.EventPriority;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;

public class Handler {
    public boolean pass, passAsync;

    @SubscribeEvent
    public void onEvent(TestEvent event) {
        pass = true;
    }

    @SubscribeEvent(priority = EventPriority.ASYNC)
    public void onEventAsync(TestEvent event) {
        passAsync = true;
    }
}
