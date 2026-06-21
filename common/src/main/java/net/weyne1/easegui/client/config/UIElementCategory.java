package net.weyne1.easegui.client.config;

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
}