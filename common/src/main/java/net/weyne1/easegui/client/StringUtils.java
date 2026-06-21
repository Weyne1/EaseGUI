package net.weyne1.easegui.client;

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
}
