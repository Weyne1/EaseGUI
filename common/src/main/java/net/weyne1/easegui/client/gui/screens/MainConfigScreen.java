package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenGroup;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.*;
import net.weyne1.easegui.client.gui.components.FieldValidator;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;

import java.util.Comparator;
import java.util.List;

public class MainConfigScreen extends EaseGUIAbstractSplitScreen {

    public MainConfigScreen(Screen parent) {
        super(Component.translatable("easegui.main.title"), parent);
    }

    @Override
    protected Component getLeftSubtitle() {
        return Component.translatable("easegui.main.subtitle.global");
    }

    @Override
    protected Component getRightSubtitle() {
        return Component.translatable("easegui.main.subtitle.screens");
    }

    @Override
    protected void initScreen() {
        EaseGUIConfig config = ConfigManager.getConfig();
        Minecraft mc = Minecraft.getInstance();

        // ================= СЛЕВА: ГЛОБАЛЬНЫЕ НАСТРОЙКИ =================
        SettingsScrollList leftList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        leftList.setX(leftX);
        leftList.setY(50);

        leftList.addHeader(Component.translatable("easegui.config.title.blur").getString());

        EditBox blurDurationField = createTextField(String.valueOf(config.global.blurDuration));
        FieldValidator.registerLongValidator(blurDurationField, 0L, 5000L, val -> {
            config.global.blurDuration = val;
            ConfigManager.save();
        });
        blurDurationField.setEditable(config.global.enableSmoothBlur);

        Component blurState = Component.translatable(config.global.enableSmoothBlur ? "easegui.generic.on" : "easegui.generic.off");
        leftList.addButton(Button.builder(
                Component.translatable("easegui.main.smooth_blur", blurState),
                button -> {
                    config.global.enableSmoothBlur = !config.global.enableSmoothBlur;
                    Component updatedState = Component.translatable(config.global.enableSmoothBlur ? "easegui.generic.on" : "easegui.generic.off");
                    button.setMessage(Component.translatable("easegui.main.smooth_blur", updatedState));

                    blurDurationField.setEditable(config.global.enableSmoothBlur);
                    ConfigManager.save();
                }
        ).build());

        leftList.addField(Component.translatable("easegui.main.blur_duration").getString(), blurDurationField);

        Component containersBlurState = Component.translatable(config.global.blurContainers ? "easegui.generic.on" : "easegui.generic.off");
        leftList.addButton(Button.builder(
                Component.translatable("easegui.main.blur_containers", containersBlurState),
                button -> {
                    config.global.blurContainers = !config.global.blurContainers;
                    Component updatedContainersState = Component.translatable(config.global.blurContainers ? "easegui.generic.on" : "easegui.generic.off");
                    button.setMessage(Component.translatable("easegui.main.blur_containers", updatedContainersState));

                    ConfigManager.save();
                }
        ).build());

        leftList.addHeader(Component.translatable("easegui.config.title.elements").getString());

        addGlobalProfileButton(leftList, config, mc, WidgetCategory.BUTTON_LIKE, "easegui.main.button.button_like");
        addGlobalProfileButton(leftList, config, mc, WidgetCategory.TEXT, "easegui.main.button.text");
        addGlobalProfileButton(leftList, config, mc, WidgetCategory.SCROLLABLE, "easegui.main.button.scrollable");
        addGlobalProfileButton(leftList, config, mc, WidgetCategory.LIST_ENTRY, "easegui.main.button.list_entry");
        addGlobalProfileButton(leftList, config, mc, WidgetCategory.CONTAINERS, "easegui.main.button.containers");

        this.addRenderableWidget(leftList);


        // ================= СПРАВА: ОТДЕЛЬНЫЕ ЭКРАНЫ =================
        SettingsScrollList rightList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        rightList.setX(rightX);
        rightList.setY(50);

        for (EaseGUIScreenGroup category : EaseGUIScreenGroup.values()) {
            List<EaseGUIScreenType> categoryScreens = EaseGUIScreenRegistry.getRegisteredTypes().stream()
                    .filter(type -> type.getGroup() == category)
                    .sorted(Comparator.comparing(type -> type.getDisplayName().getString()))
                    .toList();

            if (!categoryScreens.isEmpty()) {
                rightList.addHeader(Component.translatable(category.getTranslationKey()).getString());

                for (EaseGUIScreenType type : categoryScreens) {
                    rightList.addButton(Button.builder(
                            type.getDisplayName(),
                            b -> mc.setScreen(new ScreenSpecificConfigScreen(this, type))
                    ).build());
                }
            }
        }

        rightList.addButton(Button.builder(
                EaseGUIScreenRegistry.OTHER.getDisplayName(),
                b -> mc.setScreen(new ScreenSpecificConfigScreen(this, EaseGUIScreenRegistry.OTHER))
        ).build());

        this.addRenderableWidget(rightList);


        // ================= НИЖНЯЯ ПАНЕЛЬ =================
        this.addRenderableWidget(Button.builder(
                Component.translatable("easegui.generic.done"),
                b -> onClose()
        ).bounds(halfWidth - 100, this.height - 30, 200, 20).build());
    }

    private void addGlobalProfileButton(SettingsScrollList list, EaseGUIConfig config, Minecraft mc, WidgetCategory category, String translationKey) {
        AnimationProfile cleanDefault = EaseGUIConfigFactory.DEFAULT_CONFIG.global.elementProfiles.get(category);
        if (cleanDefault == null) cleanDefault = new AnimationProfile();
        AnimationProfile finalCleanDefault = cleanDefault;

        list.addButton(Button.builder(
                Component.translatable(translationKey),
                button -> mc.setScreen(new ProfileEditorScreen(
                        this,
                        config.global.elementProfiles.getOrDefault(category, new AnimationProfile()),
                        finalCleanDefault,
                        category.getAllowedFeatures(),
                        updated -> {
                            config.global.elementProfiles.put(category, updated);
                            ConfigManager.save();
                        }
                ))
        ).build());
    }

    private EditBox createTextField(String value) {
        EditBox editBox = new EditBox(this.minecraft.font, 0, 0, 60, 16, Component.empty());
        editBox.setValue(value);
        return editBox;
    }
}