package net.puzzle_mod_loader.utils;

import java.util.concurrent.TimeUnit;

public class PTimer
{
    private long previousTime;
    private long prevMS;

    public PTimer() {
        previousTime = 0;
        prevMS = 0;
    }


    public boolean hasReach(long time, TimeUnit timeUnit) {
        final long convert = timeUnit.convert(System.nanoTime() - this.previousTime, TimeUnit.NANOSECONDS);
        if (convert >= time) {
            this.reset();
        }
        return convert >= time;
    }

    public boolean isTime(float time) {
        return currentTime() >= time*1000L;
    }

    public float currentTime() {
        return systemTime()-previousTime;
    }

    public void reset() {
        previousTime = systemTime();
    }

    public long systemTime() {
        return System.currentTimeMillis();
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean delay(final float milliSec) {
        return this.getTime() - this.prevMS >= milliSec;
    }
}
