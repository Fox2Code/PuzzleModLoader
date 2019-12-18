package net.puzzle_mod_loader.utils.anim;

import net.puzzle_mod_loader.utils.anim.Anim2D;

public interface Anim3D extends Anim2D {
    int getX();
    int getY();
    int getZ();
    boolean isAnimating();
    void reset();
    void doAnim();
}
