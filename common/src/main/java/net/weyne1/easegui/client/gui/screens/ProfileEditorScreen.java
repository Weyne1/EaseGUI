package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.api.animation.PivotPoint;
import net.weyne1.easegui.client.util.StringUtils;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.FieldValidator;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.preview.ProfilePreviewRenderer;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ProfileEditorScreen extends EaseGUIAbstractSplitScreen {
    private final AnimationProfile workingCopy;
    private final AnimationProfile defaultProfile;
    private final Consumer<AnimationProfile> onSave;
    private final EnumSet<ProfileFeature> activeFeatures;

    public ProfileEditorScreen(Screen parent, AnimationProfile originalProfile, AnimationProfile defaultProfile, EnumSet<ProfileFeature> features, Consumer<AnimationProfile> onSave) {
        super(Component.translatable("easegui.editor.title"), parent);
        this.onSave = onSave;
        this.activeFeatures = features;
        this.defaultProfile = defaultProfile;
        this.workingCopy = cloneProfile(originalProfile);
    }

    @Override
    protected Component getLeftSubtitle() { return Component.translatable("easegui.editor.subtitle.params"); }

    @Override
    protected Component getRightSubtitle() { return Component.translatable("easegui.editor.subtitle.preview"); }

    @Override
    protected void initScreen() {
        SettingsScrollList leftScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        leftScrollList.setX(leftX);
        leftScrollList.setY(50);

        // --- 0. Переключатель "Включено / Выключено" + кнопка "Reset" ---
        Component statusComp = workingCopy.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
        Button toggleBtn = Button.builder(
                Component.translatable("easegui.editor.button.enabled", statusComp),
                button -> {
                    workingCopy.enabled(!workingCopy.isEnabled());
                    Component newStatus = workingCopy.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
                    button.setMessage(Component.translatable("easegui.editor.button.enabled", newStatus));
                }
        ).build();

        Button resetBtn = Button.builder(
                Component.translatable("easegui.generic.reset"),
                button -> {
                    applyProfileValues(this.workingCopy, this.defaultProfile);
                    if (this.minecraft != null) {
                        this.init(this.minecraft, this.width, this.height);
                    }
                }
        ).build();

        leftScrollList.addTwoButtons(toggleBtn, resetBtn, 0.70f);

        // --- 1. Длительность (Лимит: от 0 до 5000 мс) ---
        EditBox durationField = createTextField(String.valueOf(workingCopy.getDuration()));
        FieldValidator.registerLongValidator(durationField, 0L, 5000L, workingCopy::duration);
        leftScrollList.addField(Component.translatable("easegui.editor.field.duration").getString(), durationField);

        // --- 2. Смещение (Лимит: от -1000 до 1000 пикселей) ---
        if (activeFeatures.contains(ProfileFeature.OFFSET)) {
            EditBox ox = createTextField(String.valueOf(workingCopy.getInitialOffsetX()));
            FieldValidator.registerFloatValidator(ox, -1000f, 1000f, workingCopy::initialOffsetX);

            EditBox oy = createTextField(String.valueOf(workingCopy.getInitialOffsetY()));
            FieldValidator.registerFloatValidator(oy, -1000f, 1000f, workingCopy::initialOffsetY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.offset").getString(), ox, oy);
        }

        // --- 3. Масштаб (Лимит: от 0.0 до 10.0 крат) ---
        if (activeFeatures.contains(ProfileFeature.SCALE)) {
            EditBox sx = createTextField(String.valueOf(workingCopy.getInitialScaleX()));
            FieldValidator.registerFloatValidator(sx, 0.0f, 10.0f, workingCopy::initialScaleX);

            EditBox sy = createTextField(String.valueOf(workingCopy.getInitialScaleY()));
            FieldValidator.registerFloatValidator(sy, 0.0f, 10.0f, workingCopy::initialScaleY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.scale").getString(), sx, sy);
        }

        // --- 4. Прозрачность (Лимит: от 0.0 до 1.0) ---
        if (activeFeatures.contains(ProfileFeature.ALPHA)) {
            EditBox alphaField = createTextField(String.valueOf(workingCopy.getInitialAlpha()));
            FieldValidator.registerFloatValidator(alphaField, 0.0f, 1.0f, workingCopy::initialAlpha);
            leftScrollList.addField(Component.translatable("easegui.editor.field.alpha").getString(), alphaField);
        }

        // --- 5. Каскадность (Лимит: от 0 до 1000 мс) ---
        if (activeFeatures.contains(ProfileFeature.CASCADE_DELAY)) {
            EditBox cascadeField = createTextField(String.valueOf(workingCopy.getCascadeDelay()));
            FieldValidator.registerLongValidator(cascadeField, 0L, 1000L, workingCopy::cascadeDelay);
            leftScrollList.addField(Component.translatable("easegui.editor.field.cascade_delay").getString(), cascadeField);
        }

        // --- 5.1. Направление каскада
        if (activeFeatures.contains(ProfileFeature.CASCADE_DIRECTION)) {
            Component dirComp = workingCopy.getCascadeDirection().getDisplayName();
            leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.cascade_dir", dirComp), b -> {
                CascadeDirection[] v = CascadeDirection.values();
                workingCopy.cascadeDirection(v[(workingCopy.getCascadeDirection().ordinal() + 1) % v.length]);
                b.setMessage(Component.translatable("easegui.editor.button.cascade_dir", workingCopy.getCascadeDirection().getDisplayName()));
            }).build());
        }

        // --- 6. Точка опоры (Pivot) ---
        if (activeFeatures.contains(ProfileFeature.PIVOT)) {
            Component pivotComp = Component.translatable("easegui.pivot." + workingCopy.getPivot().name().toLowerCase());
            leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.pivot", pivotComp), b -> {
                PivotPoint[] v = PivotPoint.values();
                workingCopy.pivot(v[(workingCopy.getPivot().ordinal() + 1) % v.length]);
                Component updatedPivot = Component.translatable("easegui.pivot." + workingCopy.getPivot().name().toLowerCase());
                b.setMessage(Component.translatable("easegui.editor.button.pivot", updatedPivot));
            }).build());
        }

        // --- 7. Интерполяция (Easing) ---
        Component easingComp = Component.literal(StringUtils.toTitleCase(workingCopy.getEasing()));
        leftScrollList.addWidget(Button.builder(
                Component.translatable("easegui.editor.button.easing", easingComp),
                button -> {
                    EasingType[] values = EasingType.values();
                    workingCopy.easing(values[(workingCopy.getEasing().ordinal() + 1) % values.length]);
                    Component updatedEasing = Component.literal(StringUtils.toTitleCase(workingCopy.getEasing()));
                    button.setMessage(Component.translatable("easegui.editor.button.easing", updatedEasing));
                }
        ).build());

        this.addRenderableWidget(leftScrollList);

        // Кнопки управления снизу
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.save"), b -> saveAndClose()).bounds(halfWidth - 105, this.height - 30, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.cancel"), b -> onClose()).bounds(halfWidth + 5, this.height - 30, 100, 20).build());
    }

    private EditBox createTextField(String value) {
        assert this.minecraft != null;
        EditBox editBox = new EditBox(this.minecraft.font, 0, 0, 60, 16, Component.empty());
        editBox.setValue(value);
        return editBox;
    }

    private void saveAndClose() {
        this.onSave.accept(this.workingCopy);
        onClose();
    }

    @Override
    protected void renderOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ProfilePreviewRenderer.render(graphics, this.font, this.width, this.height, this.workingCopy, this.activeFeatures);
    }
}