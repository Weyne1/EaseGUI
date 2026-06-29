package net.weyne1.easegui.client.gui.configurator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.screens.ProfileEditorScreen;

import java.util.EnumSet;

public class AdvancementsScreenConfigurator implements IScreenConfigurator {

    @Override
    public void populate(SettingsScrollList list, EaseGUIConfig.ScreenSettings settings, Screen parentScreen) {
        Minecraft mc = Minecraft.getInstance();
        if (settings.advancements == null) return;
        var adv = settings.advancements;

        // --- ОКНО ДОСТИЖЕНИЙ ---
        list.addHeader(Component.translatable("easegui.config.advancements.window.header").getString());

        AnimationProfile defaultWindow = new EaseGUIConfig.AdvancementsSettings().windowProfile;

        list.addButton(Button.builder(Component.translatable("easegui.config.advancements.window.edit_anim"), btn ->
                mc.setScreen(new ProfileEditorScreen(parentScreen, adv.windowProfile, defaultWindow, EnumSet.of(ProfileFeature.OFFSET, ProfileFeature.SCALE, ProfileFeature.ALPHA, ProfileFeature.PIVOT), updated -> {
                    adv.windowProfile = updated; ConfigManager.save();
                }))
        ).build());

        // --- ВКЛАДКИ ДОСТИЖЕНИЙ ---
        list.addHeader(Component.translatable("easegui.config.advancements.tabs.header").getString());

        AnimationProfile defaultTabs = new EaseGUIConfig.AdvancementsSettings().tabsProfile;

        list.addButton(Button.builder(Component.translatable("easegui.config.advancements.tabs.edit_anim"), btn ->
                mc.setScreen(new ProfileEditorScreen(parentScreen, adv.tabsProfile, defaultTabs, EnumSet.of(ProfileFeature.ALPHA, ProfileFeature.CASCADE_DELAY), updated -> {
                    adv.tabsProfile = updated; ConfigManager.save();
                }))
        ).build());
    }
}