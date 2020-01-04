package net.puzzle_mod_loader.core;

import com.google.common.collect.ImmutableList;
import net.puzzle_mod_loader.utils.DesktopUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Contributors {
    public static final List<Contributor> contributors;

    static {
        ArrayList<Contributor> contributorsList = new ArrayList<>();
        contributorsList.add(new Contributor("Fox2Code"    , "https://github.com/Fox2Code"));
        contributorsList.add(new Contributor("Fernixx"  , "https://github.com/Fernixx"));
        contributors = ImmutableList.copyOf(contributorsList);
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
