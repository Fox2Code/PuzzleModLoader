package net.puzzle_mod_loader.events;

public class Event {
    private boolean canceled = false;

    public final boolean isCancelable() {
        return this instanceof Cancelable;
    }

    public final boolean isCanceled() {
        return canceled;
    }

    public final void setCanceled(boolean canceled) {
        if (this instanceof Cancelable) {
            this.canceled = canceled;
        } else {
            throw new UnsupportedOperationException("Call on Event.setCanceled() on a non Cancelable Event");
        }
    }

    /**
     * Post is called after event was passed to all mods
     */
    protected void post() {}

    public interface Cancelable {
        boolean isCancelable();

        boolean isCanceled();

        void setCanceled(boolean canceled);
    }
}
