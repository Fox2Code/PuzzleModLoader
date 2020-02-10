package net.puzzle_mod_loader.helper;

public class ModFlags {
    /**
     * Indicate that the mod is a loader
     */
    public static final String LOADER = "LOADER";
    /**
     * Indicate that the mod is in development (Probably open in the IDE)
     * Not receiving the hash for the mod containing this flag is common
     * Do not ban users with DEV or missing flags (this will make you ban from the API)
     */
    public static final String DEV = "DEV";
    /**
     * Indicate that the mod is in development/debug mode
     */
    public static final String DEV_MODE = "DEV_MODE";
}
