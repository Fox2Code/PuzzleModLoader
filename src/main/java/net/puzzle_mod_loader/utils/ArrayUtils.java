package net.puzzle_mod_loader.utils;

import java.util.Arrays;

public class ArrayUtils {
    @SafeVarargs
    public static <T> T[] append(T[] array, T... objects) {
        if (array.length == 0) return objects;
        if (objects.length == 0) return array;
        T[] newArray = Arrays.copyOf(array, array.length + objects.length);
        System.arraycopy(objects, 0, newArray, array.length, objects.length);
        return newArray;
    }

    @SafeVarargs
    public static <T> T[] prepend(T[] array, T... objects) {
        return append(objects, array); // Lazy hack :3
    }
}
