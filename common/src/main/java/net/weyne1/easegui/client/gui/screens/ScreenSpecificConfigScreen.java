package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.*;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.configurator.IScreenConfigurator;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ScreenSpecificConfigScreen extends EaseGUIAbstractSplitScreen {
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
        EaseGUIConfig config = ConfigManager.getConfig();
        EaseGUIConfig.ScreenSettings settings = config.screens.get(screenType.getId());

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
            if ("other".equals(screenType.getId())) {
                Component warningText = Component.translatable("easegui.gui.warning.title")
                        .append("\n\n")
                        .append(Component.translatable("easegui.gui.warning.other_desc"));

                MultiLineTextWidget warningWidget = addMultiLineTextWidget(warningText);

                this.addRenderableWidget(warningWidget);
            } else {
                StringWidget noOptionsWidget = new StringWidget(Component.translatable("easegui.gui.no_unique_options"), this.font);
                int leftBlockCenterX = leftX + (listWidth / 2);
                noOptionsWidget.setX(leftBlockCenterX - (noOptionsWidget.getWidth() / 2));
                noOptionsWidget.setY(this.height / 2 - 4);
                noOptionsWidget.setColor(0x55FFFFFF);
                this.addRenderableWidget(noOptionsWidget);
            }
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

    private @NotNull MultiLineTextWidget addMultiLineTextWidget(Component warningText) {
        MultiLineTextWidget warningWidget = new MultiLineTextWidget(warningText, this.font);

        int padding = 20;
        warningWidget.setMaxWidth(listWidth - (padding * 2));
        warningWidget.setCentered(true);
        int leftBlockCenterX = leftX + (listWidth / 2);
        warningWidget.setX(leftBlockCenterX - (warningWidget.getWidth() / 2));
        warningWidget.setY((this.height / 2) - (warningWidget.getHeight() / 2));
        return warningWidget;
    }

    private void setupCategoryButtons(SettingsScrollList rightScrollList, EaseGUIConfig.ScreenSettings settings, EaseGUIConfig config) {
        EnumSet<UIElementCategory> overridableCategories = EnumSet.complementOf(EnumSet.of(UIElementCategory.UNKNOWN));

        for (UIElementCategory category : overridableCategories) {
            boolean hasCustom = settings.customProfiles.containsKey(category);

            Component categoryLabel = Component.translatable("easegui.category." + category.name().toLowerCase());
            Component modeLabel = Component.translatable(hasCustom ? "easegui.generic.custom" : "easegui.generic.global");

            AnimationProfile cleanDefault = new EaseGUIConfig().global.elementProfiles.get(category);
            if (cleanDefault == null) cleanDefault = new AnimationProfile();
            AnimationProfile finalCleanDefault = cleanDefault;

            Button editBtn = Button.builder(Component.translatable("easegui.generic.configure"), btn -> {
                var profile = settings.customProfiles.getOrDefault(category, new AnimationProfile());

                EnumSet<ProfileFeature> allowedFeatures = category.getAllowedFeatures();

                Minecraft.getInstance().setScreen(new ProfileEditorScreen(this, profile, finalCleanDefault, allowedFeatures, updated -> {
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