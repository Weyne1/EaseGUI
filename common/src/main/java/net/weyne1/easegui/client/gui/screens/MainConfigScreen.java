package net.weyne1.easegui.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.EaseGUIScreenRegistry;
import net.weyne1.easegui.api.EaseGUIScreenGroup;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.*;
import net.weyne1.easegui.client.gui.components.BlurDurationSlider;
import net.weyne1.easegui.client.gui.components.DimmingIntensitySlider;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;

import java.util.Comparator;
import java.util.List;

public class MainConfigScreen extends EaseGUIAbstractSplitScreen {

    private static String lastSearchQuery = "";
    private static int dynamicListScrollAmount = 0;

    public MainConfigScreen(Screen parent) {
        super(Component.translatable("easegui.main.title"), parent);
    }

    @Override
    protected Component getLeftSubtitle() {
        return Component.translatable("easegui.main.subtitle.global");
    }

    @Override
    protected Component getRightSubtitle() {
        return null;
    }

    @Override
    protected void initScreen() {
        EaseGUIConfig config = ConfigManager.getConfig();
        Minecraft mc = Minecraft.getInstance();

        // ================= СЛЕВА: ГЛОБАЛЬНЫЕ НАСТРОЙКИ =================
        SettingsScrollList leftList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        leftList.setX(leftX);
        leftList.setY(50);

        leftList.addHeader(Component.translatable("easegui.config.title.general").getString());

        Component animationsState = Component.translatable(config.global.enabled ? "easegui.generic.on" : "easegui.generic.off");

        leftList.addWidget(Button.builder(
                        Component.translatable("easegui.main.enable_animations", animationsState),
                        button -> {
                            config.global.enabled = !config.global.enabled;

                            Component updatedState = Component.translatable(config.global.enabled ? "easegui.generic.on" : "easegui.generic.off");
                            button.setMessage(Component.translatable("easegui.main.enable_animations", updatedState));

                            ConfigManager.save();
                        }
                )
                .tooltip(Tooltip.create(Component.translatable("easegui.main.enable_animations.tooltip")))
                .build());


        leftList.addHeader(Component.translatable("easegui.config.title.blur").getString());

        BlurDurationSlider blurSlider = new BlurDurationSlider(0, 0, 0, 20, config);
        leftList.addWidget(blurSlider);

        DimmingIntensitySlider dimmingSlider = new DimmingIntensitySlider(0, 0, 0, 20, config);
        leftList.addWidget(dimmingSlider);

        Component containersBlurState = Component.translatable(config.global.blurAllTransparentScreens ? "easegui.generic.on" : "easegui.generic.off");
        leftList.addWidget(Button.builder(
                Component.translatable("easegui.main.blur_containers", containersBlurState),
                button -> {
                    config.global.blurAllTransparentScreens = !config.global.blurAllTransparentScreens;
                    Component updatedContainersState = Component.translatable(config.global.blurAllTransparentScreens ? "easegui.generic.on" : "easegui.generic.off");
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

        // ================= СПРАВА: ПОИСК И ОТДЕЛЬНЫЕ ЭКРАНЫ =================

        this.dynamicList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        this.dynamicList.setX(rightX);
        this.dynamicList.setY(50);

        int searchWidth = 160;
        int searchX = this.rightX + (this.listWidth / 2) - (searchWidth / 2);

        EditBox searchBox = new EditBox(this.font, searchX, 30, searchWidth, 16, Component.translatable("easegui.search.placeholder"));
        searchBox.setHint(Component.translatable("easegui.search.hint").withStyle(ChatFormatting.DARK_GRAY));
        searchBox.setValue(lastSearchQuery);
        searchBox.setResponder(this::onSearchQueryChanged);
        searchBox.setFocused(true);
        this.addRenderableWidget(searchBox);
        this.setFocused(searchBox);

        rebuildRightList(lastSearchQuery);
        this.addRenderableWidget(this.dynamicList);

        this.dynamicList.setScrollListener(amount -> dynamicListScrollAmount = amount);
        this.dynamicList.setScrollAmount(dynamicListScrollAmount);

        // ================= НИЖНЯЯ ПАНЕЛЬ =================
        this.addRenderableWidget(Button.builder(
                Component.translatable("easegui.generic.done"),
                b -> this.onClose()
        ).bounds(halfWidth - 100, this.height - 30, 200, 20).build());
    }

    private void onSearchQueryChanged(String query) {
        lastSearchQuery = query;
        rebuildRightList(query);
        this.dynamicList.setScrollAmount(0);
    }

    @Override
    public void onClose() {
        lastSearchQuery = "";
        dynamicListScrollAmount = 0;
        super.onClose();
    }

    private void addGlobalProfileButton(SettingsScrollList list, EaseGUIConfig config, Minecraft mc, WidgetCategory category, String translationKey) {
        AnimationProfile cleanDefault = EaseGUIConfigFactory.DEFAULT_CONFIG.global.elementProfiles.get(category);
        if (cleanDefault == null) cleanDefault = new AnimationProfile();
        AnimationProfile finalCleanDefault = cleanDefault;

        Button settingsButton = Button.builder(
                Component.translatable("easegui.generic.edit"),
                b -> mc.setScreen(new ProfileEditorScreen(
                        this,
                        config.global.elementProfiles.getOrDefault(category, new AnimationProfile()),
                        finalCleanDefault,
                        category.getAllowedFeatures(),
                        updated -> {
                            config.global.elementProfiles.put(category, updated);
                            ConfigManager.save();
                        }
                ))
        ).build();

        list.addLabelAndButton(Component.translatable(translationKey).getString(), settingsButton);
    }

    private void rebuildRightList(String query) {
        this.dynamicList.replaceEntries(List.of());

        String lowerQuery = query.toLowerCase().trim();
        EaseGUIConfig config = ConfigManager.getConfig();

        for (EaseGUIScreenGroup category : EaseGUIScreenGroup.values()) {
            List<EaseGUIScreenType> categoryScreens = EaseGUIScreenRegistry.getRegisteredTypes().stream()
                    .filter(type -> type.getGroup() == category)
                    .sorted(Comparator.comparingInt(EaseGUIScreenType::getPriority).reversed()
                            .thenComparing(type -> type.getDisplayName().getString()))
                    .toList();

            List<EaseGUIScreenType> matchingScreens = categoryScreens.stream()
                    .filter(type -> type.getDisplayName().getString().toLowerCase().contains(lowerQuery))
                    .toList();

            if (!matchingScreens.isEmpty()) {
                this.dynamicList.addHeader(category.getDisplayName().getString());

                for (EaseGUIScreenType type : matchingScreens) {
                    if (type.getGroup() == EaseGUIScreenGroup.CONTAINERS) {
                        var settings = config.screens.get(type.getId());
                        if (settings != null) {
                            addCategoryOverrideRow(this.dynamicList, type.getDisplayName(), WidgetCategory.CONTAINERS, settings, config);
                        }
                    } else {
                        Button settingsBtn = Button.builder(
                                Component.translatable("easegui.generic.edit"),
                                btn -> this.minecraft.setScreen(new ScreenSpecificConfigScreen(this, type))
                        ).build();

                        this.dynamicList.addLabelAndButton(type.getDisplayName().getString(), settingsBtn);
                    }
                }
            }
        }

        String otherName = EaseGUIScreenRegistry.OTHER.getDisplayName().getString();
        if (otherName.toLowerCase().contains(lowerQuery)) {
            Button settingsBtn = Button.builder(
                    Component.translatable("easegui.generic.edit"),
                    btn -> this.minecraft.setScreen(new ScreenSpecificConfigScreen(this, EaseGUIScreenRegistry.OTHER))
            ).build();
            this.dynamicList.addLabelAndButton(otherName, settingsBtn);
        }
    }
}