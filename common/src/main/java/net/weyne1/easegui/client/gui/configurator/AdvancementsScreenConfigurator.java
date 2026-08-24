package net.weyne1.easegui.client.gui.configurator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.config.EaseGUIConfigFactory;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.screens.ProfileEditorScreen;

import java.util.EnumSet;

public class AdvancementsScreenConfigurator implements IScreenConfigurator {

    @Override
    public void populate(SettingsScrollList list, EaseGUIConfig.ScreenSettings settings, Screen parentScreen) {
        Minecraft mc = Minecraft.getInstance();

        list.addHeader(Component.translatable("easegui.config.advancements.window.header").getString());

        var defaultSettings = EaseGUIConfigFactory.DEFAULT_CONFIG.screens.get("advancements");
        AnimationProfile defaultWindow = defaultSettings != null ? defaultSettings.windowProfile : null;
        AnimationProfile defaultTabs = defaultSettings != null ? defaultSettings.tabsProfile : null;

        list.addWidget(Button.builder(Component.translatable("easegui.config.advancements.window.edit_anim"), _ ->
                mc.setScreen(new ProfileEditorScreen(parentScreen, settings.windowProfile, defaultWindow, EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT), updated -> {
                    settings.windowProfile = updated; ConfigManager.save();
                }))
        ).build());

        list.addHeader(Component.translatable("easegui.config.advancements.tabs.header").getString());

        list.addWidget(Button.builder(Component.translatable("easegui.config.advancements.tabs.edit_anim"), _ ->
                mc.setScreen(new ProfileEditorScreen(parentScreen, settings.tabsProfile, defaultTabs, EnumSet.allOf(ProfileFeature.class), updated -> {
                    settings.tabsProfile = updated; ConfigManager.save();
                }))
        ).build());
    }
}