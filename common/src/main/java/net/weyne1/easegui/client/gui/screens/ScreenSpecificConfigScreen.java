package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.EaseGUIScreenType;
import net.weyne1.easegui.client.config.*;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.configurator.IScreenConfigurator;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ScreenSpecificConfigScreen extends EaseGUIAbstractSplitScreen {
    private final EaseGUIScreenType screenType;

    public ScreenSpecificConfigScreen(Screen parent, EaseGUIScreenType type) {
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

        int listTop = 50;
        int listBottom = this.height - 40;

        // ================= СЛЕВА =================
        SettingsScrollList leftScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, listTop, listBottom, 24);
        leftScrollList.setLeftPos(leftX);

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
        SettingsScrollList rightScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, listTop, listBottom, 24);
        rightScrollList.setLeftPos(rightX);

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
        EnumSet<WidgetCategory> overridableCategories = EnumSet.complementOf(EnumSet.of(WidgetCategory.UNKNOWN, WidgetCategory.CONTAINERS));

        for (WidgetCategory category : overridableCategories) {
            Component categoryLabel = Component.translatable("easegui.category." + category.name().toLowerCase());
            addCategoryOverrideRow(rightScrollList, categoryLabel, category, settings, config);
        }
    }
}