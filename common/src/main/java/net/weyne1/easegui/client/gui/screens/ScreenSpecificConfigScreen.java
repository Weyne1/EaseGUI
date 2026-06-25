package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.config.*;
import net.weyne1.easegui.client.gui.screens.config.IScreenConfigurator;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import java.util.EnumSet;

public class ScreenSpecificConfigScreen extends AbstractSplitScreen {
    private final ScreenType screenType;

    public ScreenSpecificConfigScreen(Screen parent, ScreenType type) {
        super(type.getDisplayName(), parent);
        this.screenType = type;
    }

    @Override
    protected Component getLeftSubtitle() { return Component.translatable("easegui.gui.subtitle.specific"); }

    @Override
    protected Component getRightSubtitle() { return Component.translatable("easegui.gui.subtitle.override"); }

    @Override
    protected void initScreen() {
        ModConfig config = ConfigManager.getConfig();
        ModConfig.ScreenSettings settings = config.screens.get(screenType.getId());

        if (settings == null) {
            Minecraft.getInstance().setScreen(this.parent);
            return;
        }

        boolean hasSpecificOptions = false;

        // ================= СЛЕВА =================
        SettingsScrollList leftScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        leftScrollList.setX(leftX);
        leftScrollList.setY(50);

        IScreenConfigurator configurator = IScreenConfigurator.get(screenType.getId());
        if (configurator != null) {
            configurator.populate(leftScrollList, settings, this);
            hasSpecificOptions = true;
        }

        this.addRenderableWidget(leftScrollList);

        if (!hasSpecificOptions) {
            Component noOptionsText = Component.translatable("easegui.gui.no_unique_options");
            StringWidget noOptionsWidget = new StringWidget(noOptionsText, this.font);
            noOptionsWidget.setX((this.halfWidth / 2) - (noOptionsWidget.getWidth() / 2));
            noOptionsWidget.setY(this.height / 2 - 4);
            noOptionsWidget.setColor(0x55FFFFFF);
            this.addRenderableWidget(noOptionsWidget);
        }

        // ================= СПРАВА =================
        SettingsScrollList rightScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        rightScrollList.setX(rightX);
        rightScrollList.setY(50);

        Component onOffState = Component.translatable(settings.enabled ? "easegui.generic.on" : "easegui.generic.off");
        rightScrollList.addButton(Button.builder(Component.translatable("easegui.gui.animate_screen", onOffState), btn -> {
            settings.enabled = !settings.enabled;
            Component updatedState = Component.translatable(settings.enabled ? "easegui.generic.on" : "easegui.generic.off");
            btn.setMessage(Component.translatable("easegui.gui.animate_screen", updatedState));
            ConfigManager.save();
        }).build());

        setupCategoryButtons(rightScrollList, settings, config);
        this.addRenderableWidget(rightScrollList);

        // Кнопка Назад
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.back"), b -> onClose())
                .bounds(halfWidth - 100, this.height - 30, 200, 20).build());
    }

    private void setupCategoryButtons(SettingsScrollList rightScrollList, ModConfig.ScreenSettings settings, ModConfig config) {
        EnumSet<UIElementCategory> overridableCategories = EnumSet.complementOf(EnumSet.of(UIElementCategory.UNKNOWN));

        for (UIElementCategory category : overridableCategories) {
            boolean hasCustom = settings.customProfiles.containsKey(category);

            Component categoryLabel = Component.translatable("easegui.category." + category.name().toLowerCase());
            Component modeLabel = Component.translatable(hasCustom ? "easegui.generic.custom" : "easegui.generic.global");

            AnimationProfile cleanDefault = new ModConfig().global.elementProfiles.get(category);
            if (cleanDefault == null) cleanDefault = new AnimationProfile();
            AnimationProfile finalCleanDefault = cleanDefault;

            Button editBtn = Button.builder(Component.translatable("easegui.generic.configure"), btn -> {
                var profile = settings.customProfiles.getOrDefault(category, new AnimationProfile());

                EnumSet<ProfileFeature> allowedFeatures = category.getAllowedFeatures();

                Minecraft.getInstance().setScreen(new EaseGUIProfileEditorScreen(this, profile, finalCleanDefault, allowedFeatures, updated -> {
                    settings.customProfiles.put(category, updated);
                    ConfigManager.save();
                }));
            }).build();
            editBtn.active = hasCustom;

            Button toggleBtn = Button.builder(Component.empty(), btn -> {
                if (settings.customProfiles.containsKey(category)) {
                    settings.customProfiles.remove(category);
                    editBtn.active = false;

                    Component globalState = Component.translatable("easegui.generic.global");
                    btn.setMessage(Component.translatable("easegui.generic.toggle_format", categoryLabel, globalState));
                } else {
                    settings.customProfiles.put(category, cloneProfile(config.global.elementProfiles.get(category)));
                    editBtn.active = true;

                    Component customState = Component.translatable("easegui.generic.custom");
                    btn.setMessage(Component.translatable("easegui.generic.toggle_format", categoryLabel, customState));
                }
                ConfigManager.save();
            }).build();

            toggleBtn.setMessage(Component.translatable("easegui.generic.toggle_format", categoryLabel, modeLabel));
            rightScrollList.addTwoButtons(toggleBtn, editBtn);
        }
    }

    private AnimationProfile cloneProfile(AnimationProfile src) {
        AnimationProfile dest = new AnimationProfile();
        if (src == null) return dest;
        return dest
                .enabled(src.enabled)
                .duration(src.duration)
                .offsetX(src.offsetX)
                .offsetY(src.offsetY)
                .startScaleX(src.startScaleX)
                .startScaleY(src.startScaleY)
                .startAlpha(src.startAlpha)
                .cascadeDelay(src.cascadeDelay)
                .easing(src.easing)
                .pivot(src.pivot)
                .cascadeDirection(src.cascadeDirection);
    }
}