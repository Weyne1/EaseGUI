package net.weyne1.easegui.client.config;

import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.AnimationProfile.EasingType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static net.weyne1.easegui.client.animation.AnimationProfile.CascadeDirection.BOTTOM_TO_TOP;
import static net.weyne1.easegui.client.animation.AnimationProfile.CascadeDirection.LEFT_TO_RIGHT;
import static net.weyne1.easegui.client.animation.AnimationProfile.EasingType.*;

public class ModConfig {
    public static final int CURRENT_SCHEMA_VERSION = 1;
    public int schemaVersion = CURRENT_SCHEMA_VERSION;

    public final GlobalSettings global = new GlobalSettings();
    public Map<String, ScreenSettings> screens = new HashMap<>();

    public ModConfig() {
        global.elementProfiles.put(UIElementCategory.BUTTON_LIKE, createButtonProfile());
        global.elementProfiles.put(UIElementCategory.TEXT, createTextProfile());
        global.elementProfiles.put(UIElementCategory.SCROLLABLE, createScrollableProfile());
        global.elementProfiles.put(UIElementCategory.LIST_ENTRY, createListEntryProfile());
        global.elementProfiles.put(UIElementCategory.CONTAINERS, createContainerProfile());

        for (ScreenType type : ScreenRegistry.getRegisteredTypes()) {
            screens.put(type.getId(), createDefaultSettingsFor(type));
        }
        screens.put(ScreenRegistry.OTHER.getId(), createDefaultSettingsFor(ScreenRegistry.OTHER));
    }

    /**
     * Patches missing structure and runs schema migrations.
     *
     * @return true if config was modified and should be saved back to disk
     */
    public boolean mergeDefaults() {
        boolean changed = false;

        if (this.schemaVersion < CURRENT_SCHEMA_VERSION) {
            this.schemaVersion = CURRENT_SCHEMA_VERSION;
            changed = true;
        }

        if (this.screens == null) {
            this.screens = new HashMap<>();
            changed = true;
        }

        for (ScreenType type : ScreenRegistry.getRegisteredTypes()) {
            if (!this.screens.containsKey(type.getId())) {
                this.screens.put(type.getId(), createDefaultSettingsFor(type));
                changed = true;
            } else {
                changed |= patchScreenSettings(type, this.screens.get(type.getId()));
            }
        }

        if (!this.screens.containsKey(ScreenRegistry.OTHER.getId())) {
            this.screens.put(ScreenRegistry.OTHER.getId(), createDefaultSettingsFor(ScreenRegistry.OTHER));
            changed = true;
        }

        return changed;
    }

    /**
     * Applies structural defaults for an existing screen entry.
     * This only fills missing nested sections, not arbitrary primitive fields.
     */
    private boolean patchScreenSettings(ScreenType type, ScreenSettings settings) {
        boolean changed = false;

        if (settings == null) {
            this.screens.put(type.getId(), createDefaultSettingsFor(type));
            return true;
        }

        if ("title".equals(type.getId())) {
            if (settings.logo == null) {
                settings.logo = createLogoSettings();
                changed = true;
            }
            if (settings.splash == null) {
                settings.splash = new SplashSettings();
                changed = true;
            }
        } else if ("advancements".equals(type.getId())) {
            if (settings.advancements == null) {
                settings.advancements = createAdvancementsSettings();
                changed = true;
            }
        }

        if (settings.customProfiles == null) {
            settings.customProfiles = new EnumMap<>(UIElementCategory.class);
            changed = true;
        }

        return changed;
    }

    private ScreenSettings createDefaultSettingsFor(ScreenType type) {
        ScreenSettings settings = new ScreenSettings();
        settings.enabled = type.isEnabledByDefault();

        switch (type.getId()) {
            case "title" -> {
                settings.logo = createLogoSettings();
                settings.splash = new SplashSettings();
            }
            case "advancements" -> settings.advancements = createAdvancementsSettings();
        }

        return settings;
    }

    public static class GlobalSettings {
        public boolean enableSmoothBlur = true;
        public long blurDuration = 300L;
        public EasingType blurEasing = EASE_OUT_CUBIC;
        public final Map<UIElementCategory, AnimationProfile> elementProfiles =
                new EnumMap<>(UIElementCategory.class);
    }

    public static class ScreenSettings {
        public boolean enabled = true;
        public Map<UIElementCategory, AnimationProfile> customProfiles =
                new EnumMap<>(UIElementCategory.class);

        public LogoSettings logo = null;
        public SplashSettings splash = null;
        public AdvancementsSettings advancements = null;
    }

    public static class LogoSettings {
        public boolean animateWholeText = false;

        public AnimationProfile logoProfile = new AnimationProfile()
                .duration(400L)
                .offsetY(10f)
                .startScaleX(0.8f)
                .startScaleY(0.8f)
                .startAlpha(0.0f)
                .cascadeDelay(60L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK)
                .pivot(AnimationProfile.PivotPoint.CENTER);

        public AnimationProfile editionProfile = new AnimationProfile()
                .duration(400L)
                .offsetY(5f)
                .startScaleX(0.9f)
                .startScaleY(0.9f)
                .startAlpha(0.0f)
                .easing(EASE_OUT_QUAD)
                .pivot(AnimationProfile.PivotPoint.CENTER);
    }

    public static class SplashSettings {
        public boolean enabled = true;
        public long splashDelay = 500L;
        public long splashDuration = 500L;
        public EasingType splashEasing = EASE_OUT_BACK;
    }

    public static class AdvancementsSettings {
        public AnimationProfile windowProfile = new AnimationProfile()
                .duration(250)
                .startAlpha(0.0f)
                .startScaleX(0.8f)
                .startScaleY(0.8f)
                .easing(EASE_OUT_CUBIC);

        public AnimationProfile tabsProfile = new AnimationProfile()
                .duration(400L)
                .offsetX(-40f)
                .offsetY(0f)
                .startAlpha(0.0f)
                .cascadeDelay(45L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK);
    }

    private static AnimationProfile createButtonProfile() {
        return new AnimationProfile()
                .duration(400)
                .offsetY(15f)
                .startAlpha(0.0f)
                .cascadeDelay(45L)
                .cascadeDirection(BOTTOM_TO_TOP)
                .easing(EASE_OUT_BACK);
    }

    private static AnimationProfile createTextProfile() {
        return new AnimationProfile()
                .duration(300)
                .startAlpha(0.0f)
                .easing(LINEAR);
    }

    private static AnimationProfile createScrollableProfile() {
        return new AnimationProfile()
                .duration(300)
                .startAlpha(0.0f)
                .easing(EASE_OUT_BACK);
    }

    private static AnimationProfile createListEntryProfile() {
        return new AnimationProfile()
                .duration(350)
                .offsetY(15f)
                .startAlpha(0.0f)
                .cascadeDelay(45L)
                .easing(EASE_OUT_CUBIC);
    }

    private static AnimationProfile createContainerProfile() {
        return new AnimationProfile()
                .duration(250)
                .offsetY(20f)
                .startAlpha(0.0f)
                .easing(EASE_OUT_CUBIC);
    }

    private static LogoSettings createLogoSettings() {
        return new LogoSettings();
    }

    private static AdvancementsSettings createAdvancementsSettings() {
        return new AdvancementsSettings();
    }
}