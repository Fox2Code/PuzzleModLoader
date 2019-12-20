package net.puzzle_mod_loader.utils.anim;

public interface Anim2D extends AnimBase {
    int getX();
    int getY();
    boolean isAnimating();
    void reset();
    void doAnim();
}
