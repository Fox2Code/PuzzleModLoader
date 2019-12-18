package net.puzzle_mod_loader.utils.anim;

public interface AnimBaseAB extends AnimBase {
    void setDirection(boolean direction);
    boolean getDirection();
    long timeLeft();
    long getDuration();
}
