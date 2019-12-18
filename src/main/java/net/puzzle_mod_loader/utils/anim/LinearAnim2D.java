package net.puzzle_mod_loader.utils.anim;

public class LinearAnim2D implements AnimAB2D {
    private final int startX;
    private final int endX;
    private final int startY;
    private final int endY;
    private final long duration;
    private long lastUpdate;
    private boolean direction;

    public LinearAnim2D(int startX, int startY, int endX, int endY, long duration) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
        this.duration = duration;
    }

    @Override
    public int getX() {
        long time = System.currentTimeMillis()-lastUpdate;
        if (time > this.duration) {
            return this.direction ? this.endX : this.startX;
        }
        int tmp = this.endX - this.startX;
        tmp = (int) (tmp*time/duration);
        return this.direction ? (this.startX + tmp) : (this.endX - tmp);
    }

    @Override
    public int getY() {
        long time = System.currentTimeMillis()-lastUpdate;
        if (time > this.duration) {
            return this.direction ? this.endY : this.startY;
        }
        int tmp = this.endY - this.startY;
        tmp = (int) (tmp*time/duration);
        return this.direction ? (this.startY + tmp) : (this.endY - tmp);
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
