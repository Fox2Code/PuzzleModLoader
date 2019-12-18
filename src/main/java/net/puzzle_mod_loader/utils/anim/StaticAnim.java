package net.puzzle_mod_loader.utils.anim;

public class StaticAnim implements Anim {
    private final int value;

    public StaticAnim(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public boolean isAnimating() {
        return false;
    }

    @Override
    public void reset() {}

    @Override
    public void doAnim() {}
}
