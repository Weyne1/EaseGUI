package net.weyne1.easegui.api;

import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.weyne1.easegui.client.config.ProfileFeature;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the logical category of a UI element, defining which animation features
 * are permitted and how classes map to these categories.
 */
public enum WidgetCategory {
    BUTTON_LIKE(EnumSet.allOf(ProfileFeature.class)),
    TEXT(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT)),
    SCROLLABLE(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA)),
    LIST_ENTRY(EnumSet.allOf(ProfileFeature.class)),
    CONTAINERS(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT)),
    EXCLUDED(EnumSet.noneOf(ProfileFeature.class)),
    UNKNOWN(EnumSet.noneOf(ProfileFeature.class));

    private static final Map<Class<?>, WidgetCategory> CUSTOM_MAPPINGS = new ConcurrentHashMap<>();

    static {
        CUSTOM_MAPPINGS.put(PageButton.class, EXCLUDED);
    }

    private final EnumSet<ProfileFeature> allowedFeatures;

    WidgetCategory(EnumSet<ProfileFeature> allowedFeatures) {
        this.allowedFeatures = allowedFeatures;
    }

    public EnumSet<ProfileFeature> getAllowedFeatures() {
        return EnumSet.copyOf(this.allowedFeatures);
    }

    /**
     * Registers a custom class mapping to a specific UI element category.
     * This allows third-party mods with custom UI frameworks to support EaseGUI animations.
     *
     * @param clazz the target widget class to map
     * @param category the category to assign to this class and its descendants
     */
    @SuppressWarnings("unused")
    public static void registerMapping(@NotNull Class<?> clazz, @NotNull WidgetCategory category) {
        CUSTOM_MAPPINGS.put(clazz, category);
    }

    /**
     * Resolves a UIElementCategory for a given class type.
     * Evaluates custom developer mappings first, then falls back to standard vanilla rules.
     *
     * @param clazz the class of the active UI element
     * @return the matched category, or {@link #UNKNOWN} if no match is found
     */
    @NotNull
    public static WidgetCategory fromClass(Class<?> clazz) {
        if (clazz == null) return UNKNOWN;

        for (Map.Entry<Class<?>, WidgetCategory> entry : CUSTOM_MAPPINGS.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }

        if (AbstractButton.class.isAssignableFrom(clazz) ||
                AbstractSliderButton.class.isAssignableFrom(clazz) ||
                EditBox.class.isAssignableFrom(clazz)) {
            return BUTTON_LIKE;
        }
        if (AbstractStringWidget.class.isAssignableFrom(clazz)) {
            return TEXT;
        }
        if (AbstractSelectionList.class.isAssignableFrom(clazz)) {
            return SCROLLABLE;
        }
        if (AbstractContainerScreen.class.isAssignableFrom(clazz)) {
            return CONTAINERS;
        }

        return UNKNOWN;
    }
}