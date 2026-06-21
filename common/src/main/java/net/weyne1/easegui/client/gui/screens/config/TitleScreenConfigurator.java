package net.weyne1.easegui.client.gui.screens.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.StringUtils;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.ModConfig;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.screens.EaseGUIProfileEditorScreen;

import java.util.EnumSet;

public class TitleScreenConfigurator implements IScreenConfigurator {

    @Override
    public void populate(SettingsScrollList list, ModConfig.ScreenSettings settings, Screen parentScreen) {
        Minecraft mc = Minecraft.getInstance();

        if (settings.logo != null) {
            var logo = settings.logo;
            list.addHeader(Component.translatable("easegui.config.title.logo.header").getString());

            // Настройка анимации букв
            list.addButton(Button.builder(Component.translatable("easegui.config.title.logo.edit_anim"), btn ->
                    mc.setScreen(new EaseGUIProfileEditorScreen(parentScreen, logo.logoProfile, EnumSet.allOf(ProfileFeature.class), updated -> {
                        logo.logoProfile = updated; ConfigManager.save();
                    }))
            ).build());

            // Тип лого (целиком / по буквам)
            Component wholeTextComp = Component.translatable(logo.animateWholeText ? "easegui.config.title.logo.type.whole" : "easegui.config.title.logo.type.letters");
            list.addButton(Button.builder(Component.translatable("easegui.config.title.logo.type", wholeTextComp), btn -> {
                logo.animateWholeText = !logo.animateWholeText;
                Component updatedComp = Component.translatable(logo.animateWholeText ? "easegui.config.title.logo.type.whole" : "easegui.config.title.logo.type.letters");
                btn.setMessage(Component.translatable("easegui.config.title.logo.type", updatedComp));
                ConfigManager.save();
            }).build());

            // Направление каскада
            Component dirComp = Component.translatable(logo.direction == ModConfig.LogoSettings.Direction.LEFT_TO_RIGHT ? "easegui.config.title.logo.direction.ltr" : "easegui.config.title.logo.direction.rtl");
            list.addButton(Button.builder(Component.translatable("easegui.config.title.logo.direction", dirComp), btn -> {
                logo.direction = (logo.direction == ModConfig.LogoSettings.Direction.LEFT_TO_RIGHT) ? ModConfig.LogoSettings.Direction.RIGHT_TO_LEFT : ModConfig.LogoSettings.Direction.LEFT_TO_RIGHT;
                Component updatedDir = Component.translatable(logo.direction == ModConfig.LogoSettings.Direction.LEFT_TO_RIGHT ? "easegui.config.title.logo.direction.ltr" : "easegui.config.title.logo.direction.rtl");
                btn.setMessage(Component.translatable("easegui.config.title.logo.direction", updatedDir));
                ConfigManager.save();
            }).build());

            // Настройка профиля Edition
            list.addButton(Button.builder(Component.translatable("easegui.config.title.edition.edit_anim"), btn ->
                    mc.setScreen(new EaseGUIProfileEditorScreen(parentScreen, logo.editionProfile, EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT), updated -> {
                        logo.editionProfile = updated; ConfigManager.save();
                    }))
            ).build());
        }

        if (settings.splash != null) {
            var splash = settings.splash;
            list.addHeader(Component.translatable("easegui.config.title.splash.header").getString());

            // Анимировать сплеш
            list.addButton(Button.builder(Component.translatable("easegui.config.title.splash.enabled", getOnOff(splash.enabled)), btn -> {
                splash.enabled = !splash.enabled;
                btn.setMessage(Component.translatable("easegui.config.title.splash.enabled", getOnOff(splash.enabled)));
                ConfigManager.save();
            }).build());

            EditBox delayField = this.createLongField(mc, String.valueOf(splash.splashDelay), 0L, 3000L, val -> {
                splash.splashDelay = val;
                ConfigManager.save();
            });
            list.addField(Component.translatable("easegui.config.title.splash.delay").getString(), delayField);

            EditBox durationField = this.createLongField(mc, String.valueOf(splash.splashDuration), 0L, 5000L, val -> {
                splash.splashDuration = val;
                ConfigManager.save();
            });
            list.addField(Component.translatable("easegui.config.title.splash.duration").getString(), durationField);

            // Интерполяция сплеша
            Component easingComp = Component.literal(StringUtils.toTitleCase(splash.splashEasing));
            list.addButton(Button.builder(Component.translatable("easegui.config.title.splash.easing", easingComp), btn -> {
                AnimationProfile.EasingType[] values = AnimationProfile.EasingType.values();
                splash.splashEasing = values[(splash.splashEasing.ordinal() + 1) % values.length];
                Component updatedEasing = Component.literal(StringUtils.toTitleCase(splash.splashEasing));
                btn.setMessage(Component.translatable("easegui.config.title.splash.easing", updatedEasing));
                ConfigManager.save();
            }).build());
        }
    }
}