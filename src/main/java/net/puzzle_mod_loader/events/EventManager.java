package net.puzzle_mod_loader.events;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.puzzle_mod_loader.launch.event.EventPriority;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventManager {
    private static final Int2ObjectOpenHashMap<List<Handler>[]> HANDLERS = new Int2ObjectOpenHashMap<>();
    private static final Executor executors = Executors.newScheduledThreadPool(4);

    public static void registerListener(Object object) {
        try {
            Class.forName("#"+object.getClass().getName()+"#Factory").getDeclaredMethod("registerHandlers", object.getClass()).invoke(null, object);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static void processEvent(final Event event) {
        List<Handler>[] HANDLER = HANDLERS.get(System.identityHashCode(event.getClass()));
        if (HANDLER == null) {
            event.post();
            return;
        }
        final int[] I = {0};
        for (final Handler handler:HANDLER[0]) {
            executors.execute(() -> {
                I[0]++;
                try {
                    handler.onEvent(event);
                } finally {
                    I[0]--;
                    synchronized (I) {
                        if (I[0] == 0) {
                            I.notify();
                        }
                    }
                }
            });
        }
        for (int i = 1;i < 6;i++) {
            for (Handler handler:HANDLER[i]) {
                handler.onEvent(event);
            }
        }
        if (I[0] != 0) {//Avoid lock if not needed (optimisation)
            synchronized (I) {
                if (I[0] != 0) {//Help avoid dead lock (stability)
                    try {
                        I.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
        event.post();
    }

    public interface Handler {
        void onEvent(Event event);
    }

    public static void registerHandler(Class<? extends Event> event, int priority, boolean ignoreCanceled, Handler handler) {
        registerHandler(event, EventPriority.values()[priority], ignoreCanceled, handler);
    }

    @SuppressWarnings({"unchecked"})
    public static void registerHandler(Class<? extends Event> event, EventPriority priority, boolean ignoreCanceled, Handler handler) {
        if (!ignoreCanceled && priority != EventPriority.ASYNC) {
            final Handler handlerOrig = handler;
            handler = e -> {
                if (!e.isCanceled()) {
                    handlerOrig.onEvent(e);
                }
            };
        }
        List<Handler>[] sets = HANDLERS.computeIfAbsent(System.identityHashCode(event), i -> new LinkedList[]{
                new LinkedList<Handler>(),
                new LinkedList<Handler>(),
                new LinkedList<Handler>(),
                new LinkedList<Handler>(),
                new LinkedList<Handler>(),
                new LinkedList<Handler>()
        });
        sets[priority.ordinal()].add(handler);
    }
}
