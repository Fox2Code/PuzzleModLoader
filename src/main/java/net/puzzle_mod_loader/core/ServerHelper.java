package net.puzzle_mod_loader.core;

import com.fox2code.udk.startup.Internal;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.utils.HashUtil;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ServerHelper {
    private static final Set<String> BLACKLIST = Sets.newHashSet();
    private static boolean onBlacklistedServer;

    /*
     * Blacklist for servers that abuse the PuzzleModLoader AC or that ban every user that have PuzzleModLoader on
     * Please don't abuse puzzle AC to ban everyone it's ridiculous, and I won't let that happen
     * This AC was made to make easier to detect cheaters not to prevent users from having mods
     */
    static {
        if (Launch.isClient()) try {
            BLACKLIST.addAll(IOUtils.readLines(new URL("http://puzzle-mod-loader.net/api/puzzle/blacklist.txt")
                    .openConnection().getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException ignored) {}
    }

    public static String displayBrand() {
        return onBlacklistedServer ? "vanilla" : "puzzle";
    }

    public static boolean isOnBlacklistedServer() {
        return onBlacklistedServer;
    }

    private static boolean isOnBlacklistedServer0() {
        ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if (serverData == null) {
            return false;
        }
        return isBlacklistedServer(serverData.ip);
    }


    private static boolean isBlacklistedServer(String server) {
        if (server == null || server.isEmpty()) {
            return false;
        }

        while (true) {
            if (isBlockedHost(server)) {
                return true;
            }
            int i = server.indexOf('.');
            if (i == -1) {
                return false;
            }
            server = server.substring(i+1);
        }
    }

    private static boolean isBlockedHost(final String server) {
        return BLACKLIST.contains(HashUtil.md5sha1(server.toLowerCase().getBytes(StandardCharsets.UTF_8)));
    }

    @Internal
    public static void updateBlacklistStatus() {
        onBlacklistedServer = isOnBlacklistedServer0();
    }
}
