package net.weyne1.easegui.client.config;

import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.animation.DirectionalAnimationProfile;
import net.weyne1.easegui.api.animation.EasingType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static net.weyne1.easegui.api.animation.EasingType.EASE_OUT_CUBIC;

public class EaseGUIConfig {
    public static final int CURRENT_SCHEMA_VERSION = 3;

    public int schemaVersion = CURRENT_SCHEMA_VERSION;
    public GlobalSettings global = new GlobalSettings();
    public Map<String, ScreenSettings> screens = new HashMap<>();

    public static class GlobalSettings {
        public boolean enabled = true;
        public boolean enableSmoothBlur = true;
        public long blurDuration = 300L;
        public boolean blurContainers = true;
        public float dimmingIntensity = 0.20f;
        public EasingType dimmingEasing = EASE_OUT_CUBIC;
        public final Map<WidgetCategory, DirectionalAnimationProfile> elementProfiles = new EnumMap<>(WidgetCategory.class);
    }

    public static class ScreenSettings {
        public boolean enabled = true;
        public Map<WidgetCategory, DirectionalAnimationProfile> customProfiles = new EnumMap<>(WidgetCategory.class);

        public LogoSettings logo = null;
        public SplashSettings splash = null;
        public AdvancementsSettings advancements = null;
    }

    public static class LogoSettings {
        public boolean animateWholeText = false;
        public DirectionalAnimationProfile logoProfile;
        public DirectionalAnimationProfile editionProfile;
    }

    public static class SplashSettings {
        public boolean enabled = true;
        public long splashDelay = 500L;
        public long splashDuration = 500L;
        public EasingType splashEasing;
    }

    public static class AdvancementsSettings {
        public DirectionalAnimationProfile windowProfile;
        public DirectionalAnimationProfile tabsProfile;
    }
}