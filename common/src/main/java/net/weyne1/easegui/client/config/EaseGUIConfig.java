package net.weyne1.easegui.client.config;

import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.animation.EasingType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static net.weyne1.easegui.client.animation.EasingType.EASE_OUT_CUBIC;

public class EaseGUIConfig {
    public static final int CURRENT_SCHEMA_VERSION = 2;

    public int schemaVersion = CURRENT_SCHEMA_VERSION;
    public GlobalSettings global = new GlobalSettings();
    public Map<String, ScreenSettings> screens = new HashMap<>();

    public static class GlobalSettings {
        public boolean enableSmoothBlur = true;
        public long blurDuration = 300L;
        public boolean blurContainers = true;
        public EasingType dimmingEasing = EASE_OUT_CUBIC;
        public final Map<EaseGUIElementCategory, AnimationProfile> elementProfiles = new EnumMap<>(EaseGUIElementCategory.class);
    }

    public static class ScreenSettings {
        public boolean enabled = true;
        public Map<EaseGUIElementCategory, AnimationProfile> customProfiles = new EnumMap<>(EaseGUIElementCategory.class);

        public LogoSettings logo = null;
        public SplashSettings splash = null;
        public AdvancementsSettings advancements = null;
    }

    public static class LogoSettings {
        public boolean animateWholeText = false;
        public AnimationProfile logoProfile;
        public AnimationProfile editionProfile;
    }

    public static class SplashSettings {
        public boolean enabled = true;
        public long splashDelay = 500L;
        public long splashDuration = 500L;
        public EasingType splashEasing;
    }

    public static class AdvancementsSettings {
        public AnimationProfile windowProfile;
        public AnimationProfile tabsProfile;
    }
}