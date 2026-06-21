package net.weyne1.easegui.client.gui;

import net.minecraft.client.gui.components.EditBox;
import java.util.function.Consumer;

public class FieldValidationHelper {

    public static final String REGEX_INT = "\\d*";
    public static final String REGEX_FLOAT = "-?\\d*\\.?\\d*";

    private static final int COLOR_VALID = 0xE0E0E0;
    private static final int COLOR_INVALID = 0xFF5555;

    public static void registerLongValidator(EditBox editBox, long min, long max, Consumer<Long> onSuccess) {
        editBox.setFilter(s -> s.matches(REGEX_INT));
        editBox.setResponder(text -> {
            if (text.isEmpty()) {
                editBox.setTextColor(COLOR_INVALID);
                return;
            }
            try {
                long value = Long.parseLong(text);
                if (value >= min && value <= max) {
                    editBox.setTextColor(COLOR_VALID);
                    onSuccess.accept(value);
                } else {
                    editBox.setTextColor(COLOR_INVALID);
                }
            } catch (NumberFormatException e) {
                editBox.setTextColor(COLOR_INVALID);
            }
        });
    }

    public static void registerFloatValidator(EditBox editBox, float min, float max, Consumer<Float> onSuccess) {
        editBox.setFilter(s -> s.matches(REGEX_FLOAT));
        editBox.setResponder(text -> {
            String clean = text.replace(',', '.');
            if (clean.isEmpty() || clean.equals("-") || clean.equals(".") || clean.equals("-.")) {
                editBox.setTextColor(COLOR_INVALID);
                return;
            }
            try {
                float value = Float.parseFloat(clean);
                if (value >= min && value <= max) {
                    editBox.setTextColor(COLOR_VALID);
                    onSuccess.accept(value);
                } else {
                    editBox.setTextColor(COLOR_INVALID);
                }
            } catch (NumberFormatException e) {
                editBox.setTextColor(COLOR_INVALID);
            }
        });
    }
}