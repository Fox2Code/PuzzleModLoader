package net.puzzle_mod_loader.launch.rebuild;

public interface ClassData {
    String getName();
    ClassData getSuperclass();
    ClassData[] getInterfaces();
    boolean isAssignableFrom(ClassData clData);
    boolean isInterface();
    boolean isFinal();
    boolean isPublic();
    boolean isCustom();
}
