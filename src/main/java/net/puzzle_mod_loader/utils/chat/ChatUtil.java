package net.puzzle_mod_loader.utils.chat;




import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.puzzle_mod_loader.compact.ClientOnly;
import net.puzzle_mod_loader.utils.ClientUtils;
import net.puzzle_mod_loader.utils.color.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$";

    public static boolean isAllowedCharacter(char character) {
        return character != 167 && character >= 32 && character != 127;
    }

    public static boolean isValidUsername(String line) {
        for(char c : line.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        return true;
    }

    @ClientOnly
    public static void sendMessage(String message) {
        if (ClientUtils.world() != null && ClientUtils.player() != null) {
            ClientUtils.player().sendMessage(makeComponent(message));
        }
    }

    public static String addFormat(final String message, final String regex) {
        return message.replaceAll("(?i)" + regex + "([0-9a-fklmnor])", "§$1");
    }

    private static TextComponent makeComponent(String message) {
        message = message.replaceAll("\t", "    ");
        for (final String key : ColorUtil.colors.keySet()) {
            final ChatColor c = ColorUtil.colors.get(key);
            message = message.replace(c.regex, "&" + c.color);
        }
        message = addFormat(message, "&");
        final String[] parts = message.split("§");
        final TextComponent icc = new TextComponent("");
        String[] array;
        for (int length = (array = parts).length, j = 0; j < length; ++j) {
            String part = array[j];
            if (part.length() > 0) {
                final char c2 = part.charAt(0);
                part = part.substring(1);
                final Style style = new Style();
                switch (c2) {
                    case 'k': {
                        style.setObfuscated(true);
                        break;
                    }
                    case 'l': {
                        style.setBold(true);
                        break;
                    }
                    case 'm': {
                        style.setUnderlined(true);
                        break;
                    }
                    case 'n': {
                        style.setStrikethrough(true);
                        break;
                    }
                    case 'o': {
                        style.setItalic(true);
                        break;
                    }
                    default: {
                        style.setColor(charToFormat(c2));
                        break;
                    }
                }
                final String[] lines = part.split("\n");
                for (int i = 0; i < lines.length; ++i) {
                    final String line = lines[i];
                    icc.append(new TextComponent(line).setStyle(style));
                    if (i != lines.length - 1) {
                        icc.append(new TextComponent("\n"));
                    }
                }
            }
        }
        return icc;
    }

    public static ChatFormatting charToFormat(final char c) {
        ChatFormatting[] values;
        for (int length = (values = ChatFormatting.values()).length, i = 0; i < length; ++i) {
            final ChatFormatting ecf = values[i];
        }
        return ChatFormatting.RESET;
    }

    public static boolean isValidEmail(String line) {
        for(char c : line.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '@' && c != '.') {
                return false;
            }
        }
        return validateEmail(line);
    }

    public static boolean validateEmail(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);

        matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public static boolean isNumber(String line) {
        try {
            Integer.parseInt(line);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String insertAt(String line, String character, int index) {
        try {
            String pref = line.substring(0, index);
            String suff = line.substring(index);
            return pref+character+suff;
        } catch (Exception e) {
            return line;
        }
    }

    public static String replaceAt(String line, String character, int start, int end) {
        start = Math.min(Math.max(0, start), line.length());
        end = Math.min(Math.max(0, end), line.length());

        String pref = line.substring(0, start>end?end:start);
        String suff = line.substring(start>end?start:end);
        return pref+character+suff;
    }


    public static Format getChatFormatter(char c) {
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <='f')) {
            return Format.COLOR;
        }
        switch (c) {
            case 'k':
                return Format.FORMAT;
            case 'l':
                return Format.FORMAT;
            case 'm':
                return Format.FORMAT;
            case 'n':
                return Format.FORMAT;
            case 'o':
                return Format.FORMAT;
            case 'r':
                return Format.FORMAT;
            default:
                return Format.NONE;
        }
    }

    public static String clearFormat(String s) {
        List<String> formats = new ArrayList<String>();

        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\u00a7') {
                formats.add(s.substring(i, Math.min(i+2, s.length())));
            }
        }

        for(String st : formats) {
            s = s.replace(st, "");
        }
        return s;
    }
}
