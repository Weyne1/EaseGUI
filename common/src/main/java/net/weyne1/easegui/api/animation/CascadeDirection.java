package net.weyne1.easegui.api.animation;

import net.minecraft.network.chat.Component;

public enum CascadeDirection {
    TOP_TO_BOTTOM("top_to_bottom"),
    BOTTOM_TO_TOP("bottom_to_top"),
    LEFT_TO_RIGHT("left_to_right"),
    RIGHT_TO_LEFT("right_to_left");

    private final String translationKey;

    CascadeDirection(String key) {
        this.translationKey = "easegui.cascade." + key;
    }

    public Component getDisplayName() {
        return Component.translatable(this.translationKey);
    }
}