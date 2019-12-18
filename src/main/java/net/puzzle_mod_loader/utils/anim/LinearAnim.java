package net.puzzle_mod_loader.utils.anim;

public class LinearAnim implements AnimAB {
    private final int start;
    private final int end;
    private final long duration;
    private long lastUpdate;
    private boolean direction;

    public LinearAnim(int start, int end, long duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    @Override
    public int getValue() {
        long time = System.currentTimeMillis()-lastUpdate;
        if (time > this.duration) {
            return this.direction ? this.end : this.start;
        }
        int tmp = this.end - this.start;
        tmp = (int) (tmp*time/duration);
        return this.direction ? (this.start + tmp) : (this.end - tmp);
    }

    @Override
    public boolean isAnimating() {
        long time = System.currentTimeMillis()-lastUpdate;
        return time < this.duration;
    }

    @Override
    public void reset() {
        this.direction = false;
        this.lastUpdate = 0;
    }

    @Override
    public void doAnim() {
        this.direction = true;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void setDirection(boolean direction) {
        if (this.direction == direction) {
            return;
        }
        long time = System.currentTimeMillis()-lastUpdate;
        long decl;
        if (time > duration) {
            decl = 0;
        } else {
            decl = duration - time;
        }
        this.lastUpdate = System.currentTimeMillis() - decl;
        this.direction = direction;
    }

    @Override
    public boolean getDirection() {
        return this.direction;
    }

    @Override
    public long timeLeft() {
        return lastUpdate + duration - System.currentTimeMillis();
    }

    @Override
    public long getDuration() {
        return duration;
    }
}
