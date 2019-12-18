package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.event.tests.TestEvent;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;

public class Handler {
    public boolean pass;

    @SubscribeEvent
    public void onEvent(TestEvent event) {
        pass = true;
    }
}
