package net.weyne1.easegui.client.config;

import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.PivotPoint;
import java.util.EnumMap;
import java.util.HashMap;

import static net.weyne1.easegui.api.animation.CascadeDirection.BOTTOM_TO_TOP;
import static net.weyne1.easegui.api.animation.CascadeDirection.LEFT_TO_RIGHT;
import static net.weyne1.easegui.api.animation.EasingType.*;

/**
 * Factory responsible for creating default configurations, applying structural patches,
 * and handling schema migrations.
 */
public final class EaseGUIConfigFactory {

    public static final EaseGUIConfig DEFAULT_CONFIG = createDefaultConfig();

    public static EaseGUIConfig createDefaultConfig() {
        EaseGUIConfig config = new EaseGUIConfig();

        config.global.elementProfiles.put(WidgetCategory.BUTTON_LIKE, createButtonProfile());
        config.global.elementProfiles.put(WidgetCategory.TEXT, createTextProfile());
        config.global.elementProfiles.put(WidgetCategory.SCROLLABLE, createScrollableProfile());
        config.global.elementProfiles.put(WidgetCategory.LIST_ENTRY, createListEntryProfile());
        config.global.elementProfiles.put(WidgetCategory.CONTAINERS, createContainerProfile());

        for (EaseGUIScreenType type : EaseGUIScreenRegistry.getRegisteredTypes()) {
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

        for (EaseGUIScreenType type : EaseGUIScreenRegistry.getRegisteredTypes()) {
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

    private static boolean patchScreenSettings(EaseGUIScreenType type, EaseGUIConfig.ScreenSettings settings, EaseGUIConfig config) {
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
            settings.customProfiles = new EnumMap<>(WidgetCategory.class);
            changed = true;
        }

        // Apply external developer animation defaults over user settings
        changed |= EaseGUIScreenRegistry.patchDefaults(type.getId(), settings);

        return changed;
    }

    private static EaseGUIConfig.ScreenSettings createDefaultSettingsFor(EaseGUIScreenType type) {
        EaseGUIConfig.ScreenSettings settings = new EaseGUIConfig.ScreenSettings();
        settings.enabled = type.isEnabledByDefault();

        switch (type.getId()) {
            case "title" -> {
                settings.logo = createLogoSettings();
                settings.splash = createSplashSettings();
            }
            case "advancements" -> settings.advancements = createAdvancementsSettings();
        }

        // Allow external mods to apply their default profile configurations on screen creation
        EaseGUIScreenRegistry.configureDefaults(type.getId(), settings);

        return settings;
    }

    private static EaseGUIConfig.LogoSettings createLogoSettings() {
        EaseGUIConfig.LogoSettings settings = new EaseGUIConfig.LogoSettings();
        settings.animateWholeText = false;
        settings.logoProfile = new AnimationProfile()
                .duration(400L)
                .initialOffsetY(10f)
                .initialScale(0.8f)
                .initialAlpha(0.0f)
                .cascadeDelay(60L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK)
                .pivot(PivotPoint.CENTER);

        settings.editionProfile = new AnimationProfile()
                .duration(400L)
                .initialOffsetY(5f)
                .initialScale(0.9f)
                .initialAlpha(0.0f)
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
                .initialAlpha(0.0f)
                .initialScale(0.8f)
                .easing(EASE_OUT_CUBIC);

        settings.tabsProfile = new AnimationProfile()
                .duration(400L)
                .initialOffsetX(-40f)
                .initialAlpha(0.0f)
                .cascadeDelay(45L)
                .cascadeDirection(LEFT_TO_RIGHT)
                .easing(EASE_OUT_BACK);
        return settings;
    }

    private static AnimationProfile createButtonProfile() {
        return new AnimationProfile()
                .duration(400)
                .initialOffsetY(15f)
                .initialAlpha(0.0f)
                .cascadeDelay(45L)
                .cascadeDirection(BOTTOM_TO_TOP)
                .easing(EASE_OUT_BACK);
    }

    private static AnimationProfile createTextProfile() {
        return new AnimationProfile()
                .duration(300)
                .initialAlpha(0.0f)
                .easing(LINEAR);
    }

    private static AnimationProfile createScrollableProfile() {
        return new AnimationProfile()
                .duration(300)
                .initialAlpha(0.0f)
                .easing(EASE_OUT_BACK);
    }

    private static AnimationProfile createListEntryProfile() {
        return new AnimationProfile()
                .duration(350)
                .initialOffsetY(15f)
                .initialAlpha(0.0f)
                .cascadeDelay(45L)
                .easing(EASE_OUT_CUBIC);
    }

    private static AnimationProfile createContainerProfile() {
        return new AnimationProfile()
                .duration(250)
                .initialOffsetY(20f)
                .initialAlpha(0.0f)
                .easing(EASE_OUT_CUBIC);
    }
}