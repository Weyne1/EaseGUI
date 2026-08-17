package net.weyne1.easegui.client.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.WidgetCategory;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.EaseGUIConfig;
import net.weyne1.easegui.client.config.EaseGUIConfigFactory;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;

public abstract class EaseGUIAbstractSplitScreen extends Screen {
    private static final int LINE_COLOR = 0x33FFFFFF;
    protected final Screen parent;
    protected int halfWidth;
    protected int listWidth;
    protected int listHeight;
    protected int leftX;
    protected int rightX;
    protected SettingsScrollList dynamicList;

    public EaseGUIAbstractSplitScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.halfWidth = this.width / 2;
        this.listWidth = halfWidth;
        this.listHeight = this.height - 50 - 45;
        this.leftX = 0;
        this.rightX = halfWidth;

        // Главный заголовок
        Component titleColored = this.title.copy().withStyle(ChatFormatting.WHITE);
        StringWidget titleWidget = new StringWidget(titleColored, this.font);
        titleWidget.setX(this.halfWidth - titleWidget.getWidth() / 2);
        titleWidget.setY(15);
        this.addRenderableWidget(titleWidget);

        // Левый подзаголовок
        Component leftSub = getLeftSubtitle();
        if (leftSub != null) {
            Component leftSubColored = leftSub.copy().withStyle(ChatFormatting.GRAY);
            StringWidget leftSubWidget = new StringWidget(leftSubColored, this.font);
            leftSubWidget.setX((this.halfWidth / 2) - (leftSubWidget.getWidth() / 2));
            leftSubWidget.setY(35);
            this.addRenderableWidget(leftSubWidget);
        }

        // Правый подзаголовок
        Component rightSub = getRightSubtitle();
        if (rightSub != null) {
            Component rightSubColored = rightSub.copy().withStyle(ChatFormatting.GRAY);
            StringWidget rightSubWidget = new StringWidget(rightSubColored, this.font);
            rightSubWidget.setX((this.halfWidth + (this.halfWidth / 2)) - (rightSubWidget.getWidth() / 2));
            rightSubWidget.setY(35);
            this.addRenderableWidget(rightSubWidget);
        }

        initScreen();
    }

    protected abstract void initScreen();

    protected abstract Component getLeftSubtitle();

    protected abstract Component getRightSubtitle();

    protected void extractOverlay(GuiGraphicsExtractor gg, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        // Вертикальный разделитель по центру экрана
        graphics.fill(halfWidth - 1, 50, halfWidth + 1, this.height - 45, LINE_COLOR);

        extractOverlay(graphics, mouseX, mouseY, partialTick);
    }

    protected void addCategoryOverrideRow(
            SettingsScrollList list,
            Component label,
            WidgetCategory category,
            EaseGUIConfig.ScreenSettings settings,
            EaseGUIConfig config
    ) {
        boolean hasCustom = settings.customProfiles.containsKey(category);

        Component modeLabel = Component.translatable(hasCustom ? "easegui.generic.custom" : "easegui.generic.global");

        AnimationProfile cleanDefault = EaseGUIConfigFactory.DEFAULT_CONFIG.global.elementProfiles.get(category);
        if (cleanDefault == null) cleanDefault = new AnimationProfile();
        AnimationProfile finalCleanDefault = cleanDefault;

        // Кнопка Настройки
        Button editBtn = Button.builder(Component.translatable("easegui.generic.edit"), _ -> {
            var profile = settings.customProfiles.getOrDefault(category, new AnimationProfile());

            EnumSet<ProfileFeature> allowedFeatures = category.getAllowedFeatures();

            Minecraft.getInstance().gui.setScreen(new ProfileEditorScreen(this, profile, finalCleanDefault, allowedFeatures, updated -> {
                settings.customProfiles.put(category, updated);
                ConfigManager.save();
            }));
        }).build();
        editBtn.active = hasCustom;

        // Кнопка Переключения (Global <-> Custom)
        Button toggleBtn = Button.builder(Component.empty(), btn -> {
            if (settings.customProfiles.containsKey(category)) {
                settings.customProfiles.remove(category);
                editBtn.active = false;

                Component globalState = Component.translatable("easegui.generic.global");
                btn.setMessage(Component.translatable("easegui.generic.toggle_format", label, globalState));
            } else {
                AnimationProfile globalProfile = config.global.elementProfiles.get(category);
                settings.customProfiles.put(category, cloneProfile(globalProfile));
                editBtn.active = true;

                Component customState = Component.translatable("easegui.generic.custom");
                btn.setMessage(Component.translatable("easegui.generic.toggle_format", label, customState));
            }
            ConfigManager.save();
        }).build();

        toggleBtn.setMessage(Component.translatable("easegui.generic.toggle_format", label, modeLabel));
        list.addTwoButtons(toggleBtn, editBtn);
    }

    protected AnimationProfile applyProfileValues(AnimationProfile target, AnimationProfile source) {
        if (target == null) target = new AnimationProfile();
        if (source == null) return target;

        return target
                .enabled(source.isEnabled())
                .duration(source.getDuration())
                .initialOffset(source.getInitialOffsetX(), source.getInitialOffsetY())
                .initialScale(source.getInitialScaleX(), source.getInitialScaleY())
                .initialAlpha(source.getInitialAlpha())
                .cascadeDelay(source.getCascadeDelay())
                .easing(source.getEasing())
                .pivot(source.getPivot())
                .cascadeDirection(source.getCascadeDirection());
    }

    protected AnimationProfile cloneProfile(AnimationProfile source) {
        return applyProfileValues(new AnimationProfile(), source);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(this.parent);
    }
}