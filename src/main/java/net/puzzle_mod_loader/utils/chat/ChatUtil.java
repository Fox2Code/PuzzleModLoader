package net.puzzle_mod_loader.utils.chat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.puzzle_mod_loader.compact.ClientOnly;
import net.puzzle_mod_loader.utils.ClientUtils;
import net.puzzle_mod_loader.utils.color.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * FNURFY BUG IS DETECTED §§§§§§ Caused by REPACKER () !
 */
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
    public static void showMessage(String message) {
        showMessage(makeComponent(message));
    }

    @ClientOnly
    public static void showMessage(Component message) {
        if (ClientUtils.world() != null && ClientUtils.player() != null) {
            ClientUtils.player().sendMessage(message, ClientUtils.player().getUUID());
        }
    }

    public static TextComponent makeComponent(String message) {
        return makeComponent(message, "§");
    }

    public static TextComponent makeComponent(String message,final String validChars) {
        TextComponent textComponent = new TextComponent("");
        if (!message.isEmpty()) {
            final StringBuilder buffer = new StringBuilder();
            Style style = Style.EMPTY;
            int index = 0;
            while (message.length() > index) {
                char c = message.charAt(index);
                if (validChars.indexOf(c) != -1) {
                    index++;
                    if (index == message.length()) {
                        buffer.append(c);
                    } else {
                        c = message.charAt(index);
                        if (validChars.indexOf(c) != -1) {
                            buffer.append(c);
                        } else {
                            ChatFormatting chatFormatting = ChatFormatting.getByCode(c);
                            if (chatFormatting != null) {
                                style = style.applyFormat(chatFormatting);
                                if (buffer.length() != 0) {
                                    textComponent.append(new TextComponent(buffer.toString()).setStyle(style));
                                    buffer.setLength(0);
                                }
                            }
                        }
                    }
                } else {
                    buffer.append(c);
                }
                index++;
            }
            if (buffer.length() != 0) {
                if (textComponent.getSiblings().isEmpty()) {
                    textComponent = (TextComponent) new TextComponent(buffer.toString()).setStyle(style);
                } else {
                    textComponent.append(new TextComponent(buffer.toString()).setStyle(style));
                }
            }
        }
        return textComponent;
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

        String pref = line.substring(0, Math.min(start, end));
        String suff = line.substring(Math.max(start, end));
        return pref+character+suff;
    }


    public static Format getChatFormatter(char c) {
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <='f')) {
            return Format.COLOR;
        }
        switch (c) {
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
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
