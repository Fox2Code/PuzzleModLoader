package net.puzzle_mod_loader.utils.anim;

public class StaticAnim3D implements Anim3D {
    private final int x;
    private final int y;
    private final int z;

    public StaticAnim3D(int x, int y,int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    public int getZ() {
        return z;
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
