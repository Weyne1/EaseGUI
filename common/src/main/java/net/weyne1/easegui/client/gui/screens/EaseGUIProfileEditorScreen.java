package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.client.StringUtils;
import net.weyne1.easegui.client.animation.AnimationProfile;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.FieldValidationHelper;

import java.util.EnumSet;
import java.util.function.Consumer;

public class EaseGUIProfileEditorScreen extends AbstractSplitScreen {
    private final AnimationProfile workingCopy;
    private final Consumer<AnimationProfile> onSave;
    private final EnumSet<ProfileFeature> activeFeatures;

    public EaseGUIProfileEditorScreen(Screen parent, AnimationProfile originalProfile, EnumSet<ProfileFeature> features, Consumer<AnimationProfile> onSave) {
        super(Component.translatable("easegui.editor.title"), parent);
        this.onSave = onSave;
        this.activeFeatures = features;
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

        // --- 0. Переключатель "Включено / Выключено" ---
        Component statusComp = workingCopy.enabled ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
        leftScrollList.addButton(Button.builder(
                Component.translatable("easegui.editor.button.enabled", statusComp),
                button -> {
                    workingCopy.enabled(!workingCopy.enabled);
                    Component newStatus = workingCopy.enabled ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
                    button.setMessage(Component.translatable("easegui.editor.button.enabled", newStatus));
                }
        ).build());

        // --- 1. Длительность (Лимит: от 0 до 5000 мс) ---
        EditBox durationField = createTextField(String.valueOf(workingCopy.duration));
        FieldValidationHelper.registerLongValidator(durationField, 0L, 5000L, workingCopy::duration);
        leftScrollList.addField(Component.translatable("easegui.editor.field.duration").getString(), durationField);

        // --- 2. Смещение (Лимит: от -1000 до 1000 пикселей) ---
        if (activeFeatures.contains(ProfileFeature.OFFSET)) {
            EditBox ox = createTextField(String.valueOf(workingCopy.offsetX));
            FieldValidationHelper.registerFloatValidator(ox, -1000f, 1000f, workingCopy::offsetX);

            EditBox oy = createTextField(String.valueOf(workingCopy.offsetY));
            FieldValidationHelper.registerFloatValidator(oy, -1000f, 1000f, workingCopy::offsetY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.offset").getString(), ox, oy);
        }

        // --- 3. Масштаб (Лимит: от 0.0 до 10.0 крат) ---
        if (activeFeatures.contains(ProfileFeature.SCALE)) {
            EditBox sx = createTextField(String.valueOf(workingCopy.startScaleX));
            FieldValidationHelper.registerFloatValidator(sx, 0.0f, 10.0f, workingCopy::startScaleX);

            EditBox sy = createTextField(String.valueOf(workingCopy.startScaleY));
            FieldValidationHelper.registerFloatValidator(sy, 0.0f, 10.0f, workingCopy::startScaleY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.scale").getString(), sx, sy);
        }

        // --- 4. Прозрачность (Лимит: от 0.0 до 1.0) ---
        if (activeFeatures.contains(ProfileFeature.ALPHA)) {
            EditBox alphaField = createTextField(String.valueOf(workingCopy.startAlpha));
            FieldValidationHelper.registerFloatValidator(alphaField, 0.0f, 1.0f, workingCopy::startAlpha);
            leftScrollList.addField(Component.translatable("easegui.editor.field.alpha").getString(), alphaField);
        }

        // --- 5. Каскадность (Лимит: от 0 до 1000 мс) ---
        if (activeFeatures.contains(ProfileFeature.CASCADE_DELAY)) {
            EditBox cascadeField = createTextField(String.valueOf(workingCopy.cascadeDelay));
            FieldValidationHelper.registerLongValidator(cascadeField, 0L, 1000L, workingCopy::cascadeDelay);
            leftScrollList.addField(Component.translatable("easegui.editor.field.cascade_delay").getString(), cascadeField);
        }

        // --- 5.1. Направление каскада
        if (activeFeatures.contains(ProfileFeature.CASCADE_DIRECTION)) {
            Component dirComp = getCascadeDirectionComponent(workingCopy.cascadeDirection);
            leftScrollList.addButton(Button.builder(Component.translatable("easegui.editor.button.cascade_dir", dirComp), b -> {
                AnimationProfile.CascadeDirection[] v = AnimationProfile.CascadeDirection.values();
                workingCopy.cascadeDirection(v[(workingCopy.cascadeDirection.ordinal() + 1) % v.length]);
                b.setMessage(Component.translatable("easegui.editor.button.cascade_dir", getCascadeDirectionComponent(workingCopy.cascadeDirection)));
            }).build());
        }

        // --- 6. Точка опоры (Pivot) ---
        if (activeFeatures.contains(ProfileFeature.PIVOT)) {
            Component pivotComp = Component.translatable("easegui.pivot." + workingCopy.pivot.name().toLowerCase());
            leftScrollList.addButton(Button.builder(Component.translatable("easegui.editor.button.pivot", pivotComp), b -> {
                AnimationProfile.PivotPoint[] v = AnimationProfile.PivotPoint.values();
                workingCopy.pivot(v[(workingCopy.pivot.ordinal() + 1) % v.length]);
                Component updatedPivot = Component.translatable("easegui.pivot." + workingCopy.pivot.name().toLowerCase());
                b.setMessage(Component.translatable("easegui.editor.button.pivot", updatedPivot));
            }).build());
        }

        // --- 7. Интерполяция (Easing) ---
        Component easingComp = Component.literal(StringUtils.toTitleCase(workingCopy.easing));
        leftScrollList.addButton(Button.builder(
                Component.translatable("easegui.editor.button.easing", easingComp),
                button -> {
                    AnimationProfile.EasingType[] values = AnimationProfile.EasingType.values();
                    workingCopy.easing(values[(workingCopy.easing.ordinal() + 1) % values.length]);
                    Component updatedEasing = Component.literal(StringUtils.toTitleCase(workingCopy.easing));
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

    private Component getCascadeDirectionComponent(AnimationProfile.CascadeDirection dir) {
        return Component.translatable(switch (dir) {
            case TOP_TO_BOTTOM -> "easegui.cascade.top_to_bottom";
            case BOTTOM_TO_TOP -> "easegui.cascade.bottom_to_top";
            case LEFT_TO_RIGHT -> "easegui.cascade.left_to_right";
            case RIGHT_TO_LEFT -> "easegui.cascade.right_to_left";
        });
    }

    private void saveAndClose() {
        this.onSave.accept(this.workingCopy);
        onClose();
    }

    @Override
    protected void renderOverlay(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        ProfilePreviewRenderer.render(gg, this.font, this.width, this.height, this.workingCopy, this.activeFeatures);
    }

    private AnimationProfile cloneProfile(AnimationProfile s) {
        if (s == null) {
            return new AnimationProfile();
        }
        return new AnimationProfile()
                .enabled(s.enabled)
                .duration(s.duration)
                .offsetX(s.offsetX)
                .offsetY(s.offsetY)
                .startScaleX(s.startScaleX)
                .startScaleY(s.startScaleY)
                .startAlpha(s.startAlpha)
                .cascadeDelay(s.cascadeDelay)
                .easing(s.easing)
                .pivot(s.pivot)
                .cascadeDirection(s.cascadeDirection);
    }
}