package net.puzzle_mod_loader.helper;

public final class ModInfo {
    public static final String[] NO_FLAGS = new String[0];

    public final String id, display, version, hash, signature, flags;

    public ModInfo(String id, String display, String version, String hash,String signature,String flags) {
        this.id = id;
        this.display = display;
        this.version = version;
        this.hash = hash;
        this.signature = signature;
        this.flags = flags;
    }

    public String[] getFlags() {
        return this.flags.isEmpty() ? NO_FLAGS : this.flags.split(",");
    }

    public boolean hasFlag(String flag) {
        //TODO Optimise this method
        if (flags.length() <= flag.length()) {
            return flags.equals(flag);
        } else {
            return flags.startsWith(flag+",") || flags.endsWith(","+flag) || flags.contains(","+flag+",");
        }
    }
}
