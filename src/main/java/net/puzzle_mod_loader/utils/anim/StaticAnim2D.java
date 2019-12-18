package net.puzzle_mod_loader.utils.anim;

public class StaticAnim2D implements Anim2D {
    private final int x;
    private final int y;

    public StaticAnim2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
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
