package net.puzzle_mod_loader.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class RotationUtils
{
    public static float[] getRotations(final Entity origin,final Entity ent) {
        final double x = ent.getX();
        final double z = ent.getZ();
        final double y = ent.getY() + ent.getEyeHeight() / 2.0f;
        return getRotationFromPosition(origin,x, z, y);
    }

    public static float[] getAverageRotations(final Entity origin, final List<Entity> targetList) {
        double posX = 0.0;
        double posY = 0.0;
        double posZ = 0.0;
        for (final Entity ent : targetList) {
            posX += ent.getX();
            posY += ent.getBoundingBox().maxY - 2.0;
            posZ += ent.getZ();
        }
        posX /= targetList.size();
        posY /= targetList.size();
        posZ /= targetList.size();
        return getRotationFromPosition(origin,posX, posZ, posY);
    }

    public static float[] getRotationFromPosition(Entity origin, final double x, final double z, final double y) {
        final double xDiff = x - origin.getX();
        final double zDiff = z - origin.getZ();
        final double yDiff = y - origin.getY() - 0.6;
        final double dist = Mth.sqrt(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }

    public static float getTrajAngleSolutionLow(final float d3, final float d1, final float velocity) {
        final float g = 0.006f;
        final float sqrt = velocity * velocity * velocity * velocity - g * (g * (d3 * d3) + 2.0f * d1 * (velocity * velocity));
        return (float)Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(sqrt)) / (g * d3)));
    }

    public static float getNewAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public static float getDistanceBetweenAngles(final float angle1, final float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 360.0f - angle3;
        }
        return angle3;
    }
}
