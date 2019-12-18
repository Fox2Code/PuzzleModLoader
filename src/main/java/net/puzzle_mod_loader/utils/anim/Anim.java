package net.puzzle_mod_loader.utils.anim;

public interface Anim extends AnimBase {
    int getValue();
    boolean isAnimating();
    void reset();
    void doAnim();
}
