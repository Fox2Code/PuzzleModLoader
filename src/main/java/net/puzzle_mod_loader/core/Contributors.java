package net.puzzle_mod_loader.core;

import net.puzzle_mod_loader.utils.DesktopUtil;

import java.io.IOException;
import java.util.ArrayList;

public class Contributors {
    public static final ArrayList<Contributor> contributors = new ArrayList<>();

    static {
        contributors.add(new Contributor("Fox2Code"    , "https://github.com/Fox2Code"));
        contributors.add(new Contributor("Furry2Code"  , "https://github.com/Furry2Code"));
    }

    public static final class Contributor {
        private final String name;
        private final String github;

        private Contributor(String name,String github) {
            this.name = name;
            this.github = github;
        }

        public String getName() {
            return name;
        }

        public String getGithub() {
            return github;
        }

        public void openGithub() {
            try {
                DesktopUtil.openURL(github);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
