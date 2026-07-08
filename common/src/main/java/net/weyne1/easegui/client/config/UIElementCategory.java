package net.weyne1.easegui.client.config;

import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public enum UIElementCategory {
    BUTTON_LIKE(EnumSet.allOf(ProfileFeature.class)),
    TEXT(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT)),
    SCROLLABLE(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA)),
    LIST_ENTRY(EnumSet.allOf(ProfileFeature.class)),
    CONTAINERS(EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT)),
    UNKNOWN(EnumSet.noneOf(ProfileFeature.class));

    private final EnumSet<ProfileFeature> allowedFeatures;

    UIElementCategory(EnumSet<ProfileFeature> allowedFeatures) {
        this.allowedFeatures = allowedFeatures;
    }

    public EnumSet<ProfileFeature> getAllowedFeatures() {
        return EnumSet.copyOf(this.allowedFeatures);
    }

    @NotNull public static UIElementCategory fromClass(Class<?> clazz) {
        if (AbstractButton.class.isAssignableFrom(clazz) || AbstractSliderButton.class.isAssignableFrom(clazz) || EditBox.class.isAssignableFrom(clazz)) {
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