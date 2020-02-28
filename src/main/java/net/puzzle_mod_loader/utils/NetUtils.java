package net.puzzle_mod_loader.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetUtils
{
    private static final JsonParser jsonParser = new JsonParser();
    public static void openWeb(String s) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(s));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {}
    }

    public static java.util.List<String> usernameHistory(String username) {
        String uuid = null;
        java.util.List<String> names = new ArrayList<String>();
        try {
            uuid = getUUIDFromName(username);
        } catch (Exception e) {}

        if (uuid == null) {
            return names;
        }

        try {
            names = getNamesFromUUID(uuid);
        } catch (Exception e) {}

        return names;
    }

    public static String getUUIDFromName(String username) throws Exception {
        URL getUUID = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        BufferedReader in = new BufferedReader(new InputStreamReader(getUUID.openStream()));

        String inputLine;
        StringBuilder line = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            line.append(inputLine);
        }
        in.close();


        return jsonParser.parse(line.toString()).getAsJsonObject().get("id").getAsString();
    }

    public static java.util.List<String> getNamesFromUUID(String uuid) throws Exception {
        List<String> names = new ArrayList<String>();
        URL getNames = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
        BufferedReader in = new BufferedReader(new InputStreamReader(getNames.openStream()));

        String inputLine = "";
        String line = "";
        while ((inputLine = in.readLine()) != null) {
            line = inputLine;
        }
        in.close();

        JsonArray ja = new JsonArray();

        for(int i = 0; i < ja.size(); i++) {
            JsonObject jao = (JsonObject) new JsonObject().get(ja.get(i).toString());
            names.add(jao.get("name").getAsString());
        }

        return names;
    }
}
