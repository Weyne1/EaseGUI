package net.weyne1.easegui.client.util;

import java.util.Locale;

public class StringUtils {
    public static String toTitleCase(Enum<?> e) {
        if (e == null) return "";
        String name = e.name();
        StringBuilder sb = new StringBuilder(name.length());
        boolean capitalizeNext = true;

        for (char c : name.toCharArray()) {
            if (c == '_') {
                sb.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) return input;
        if (input.equals(input.toUpperCase(Locale.ROOT))) {
            return input.toLowerCase(Locale.ROOT);
        }
        return input.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }
}
