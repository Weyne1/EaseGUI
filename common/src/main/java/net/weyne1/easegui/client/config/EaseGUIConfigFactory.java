package net.weyne1.easegui.client.config;

import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.PivotPoint;
import java.util.EnumMap;
import java.util.HashMap;

import static net.weyne1.easegui.client.animation.CascadeDirection.BOTTOM_TO_TOP;
import static net.weyne1.easegui.client.animation.CascadeDirection.LEFT_TO_RIGHT;
import static net.weyne1.easegui.client.animation.EasingType.*;

/**
 * Factory responsible for creating default configurations, applying structural patches,
 * and handling schema migrations.
 */
public final class EaseGUIConfigFactory {

    public static EaseGUIConfig createDefaultConfig() {
        EaseGUIConfig config = new EaseGUIConfig();

        config.global.elementProfiles.put(UIElementCategory.BUTTON_LIKE, createButtonProfile());
        config.global.elementProfiles.put(UIElementCategory.TEXT, createTextProfile());
        config.global.elementProfiles.put(UIElementCategory.SCROLLABLE, createScrollableProfile());
        config.global.elementProfiles.put(UIElementCategory.LIST_ENTRY, createListEntryProfile());
        config.global.elementProfiles.put(UIElementCategory.CONTAINERS, createContainerProfile());

        for (ScreenType type : EaseGUIScreenRegistry.getRegisteredTypes()) {
            config.screens.put(type.getId(), createDefaultSettingsFor(type));
        }
        config.screens.put(EaseGUIScreenRegistry.OTHER.getId(), createDefaultSettingsFor(EaseGUIScreenRegistry.OTHER));

        return config;
    }

    /**
     * Patches missing structures in an existing config and bumps the schema version if needed.
     * Replaces the old non-SRP mergeDefaults() inside the config class.
     *
     * @return true if config was modified and should be saved back to disk
     */
    public static boolean mergeDefaults(EaseGUIConfig config) {
        boolean changed = false;

        if (config.schemaVersion < EaseGUIConfig.CURRENT_SCHEMA_VERSION) {
            config.schemaVersion = EaseGUIConfig.CURRENT_SCHEMA_VERSION;
            changed = true;
        }

        if (config.screens == null) {
            config.screens = new HashMap<>();
            changed = true;
        }

        for (ScreenType type : EaseGUIScreenRegistry.getRegisteredTypes()) {
            if (!config.screens.containsKey(type.getId())) {
                config.screens.put(type.getId(), createDefaultSettingsFor(type));
                changed = true;
            } else {
                changed |= patchScreenSettings(type, config.screens.get(type.getId()), config);
            }
        }

        if (!config.screens.containsKey(EaseGUIScreenRegistry.OTHER.getId())) {
            config.screens.put(EaseGUIScreenRegistry.OTHER.getId(), createDefaultSettingsFor(EaseGUIScreenRegistry.OTHER));
            changed = true;
        }

        return changed;
    }

    private static boolean patchScreenSettings(ScreenType type, EaseGUIConfig.ScreenSettings settings, EaseGUIConfig config) {
        boolean changed = false;

        if (settings == null) {
            config.screens.put(type.getId(), createDefaultSettingsFor(type));
            return true;
        }

        if ("title".equals(type.getId())) {
            if (settings.logo == null) {
                settings.logo = createLogoSettings();
                changed = true;
            }
            if (settings.splash == null) {
                settings.splash = createSplashSettings();
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

    private static EaseGUIConfig.ScreenSettings createDefaultSettingsFor(ScreenType type) {
        EaseGUIConfig.ScreenSettings settings = new EaseGUIConfig.ScreenSettings();
        settings.enabled = type.isEnabledByDefault();

        switch (type.getId()) {
            case "title" -> {
                settings.logo = createLogoSettings();
                settings.splash = createSplashSettings();
            }
            case "advancements" -> settings.advancements = createAdvancementsSettings();
        }

        return settings;
    }

    private static EaseGUIConfig.LogoSettings createLogoSettings() {
        EaseGUIConfig.LogoSettings settings = new EaseGUIConfig.LogoSettings();
        settings.animateWholeText = false;
        settings.logoProfile = new AnimationProfile()
                .duration(400L)
                .offsetY(10f)
                .startScale(0.8f)
                .startAlpha(0.0f)
                .cascadeDelay(60L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK)
                .pivot(PivotPoint.CENTER);

        settings.editionProfile = new AnimationProfile()
                .duration(400L)
                .offsetY(5f)
                .startScale(0.9f)
                .startAlpha(0.0f)
                .easing(EASE_OUT_QUAD)
                .pivot(PivotPoint.CENTER);
        return settings;
    }

    private static EaseGUIConfig.SplashSettings createSplashSettings() {
        EaseGUIConfig.SplashSettings settings = new EaseGUIConfig.SplashSettings();
        settings.enabled = true;
        settings.splashDelay = 500L;
        settings.splashDuration = 500L;
        settings.splashEasing = EASE_OUT_BACK;
        return settings;
    }

    private static EaseGUIConfig.AdvancementsSettings createAdvancementsSettings() {
        EaseGUIConfig.AdvancementsSettings settings = new EaseGUIConfig.AdvancementsSettings();
        settings.windowProfile = new AnimationProfile()
                .duration(250)
                .startAlpha(0.0f)
                .startScale(0.8f)
                .easing(EASE_OUT_CUBIC);

        settings.tabsProfile = new AnimationProfile()
                .duration(400L)
                .offsetX(-40f)
                .startAlpha(0.0f)
                .cascadeDelay(45L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK);
        return settings;
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
}