package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.api.animation.EasingType;
import net.weyne1.easegui.api.animation.PivotPoint;
import net.weyne1.easegui.client.config.ProfileFeature;
import net.weyne1.easegui.client.gui.components.FieldValidator;
import net.weyne1.easegui.client.gui.components.SettingsScrollList;
import net.weyne1.easegui.client.gui.preview.ProfilePreviewRenderer;
import net.weyne1.easegui.client.util.StringUtils;

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
    protected Component getLeftSubtitle() {
        return Component.translatable("easegui.editor.subtitle.params");
    }

    @Override
    protected Component getRightSubtitle() {
        return Component.translatable("easegui.editor.subtitle.preview");
    }

    @Override
    protected void initScreen() {
        SettingsScrollList leftScrollList = new SettingsScrollList(this.minecraft, listWidth, listHeight, 50, 24);
        leftScrollList.setX(leftX);
        leftScrollList.setY(50);

        Component statusComp = workingCopy.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
        Button toggleBtn = Button.builder(
                        Component.translatable("easegui.editor.button.enabled", statusComp), button -> {
                            workingCopy.enabled(!workingCopy.isEnabled());
                            Component newStatus = workingCopy.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
                            button.setMessage(Component.translatable("easegui.editor.button.enabled", newStatus));
                        })
                .tooltip(Tooltip.create(Component.translatable("easegui.editor.button.enabled.tooltip")))
                .build();

        Button resetBtn = Button.builder(
                        Component.translatable("easegui.generic.reset"), _ -> {
                            applyProfileValues(this.workingCopy, this.defaultProfile);
                            this.init(this.width, this.height);
                        })
                .tooltip(Tooltip.create(Component.translatable("easegui.editor.button.reset.tooltip")))
                .build();

        leftScrollList.addTwoButtons(toggleBtn, resetBtn, 0.70f);

        EditBox durationField = createTextField(String.valueOf(workingCopy.getDuration()));
        durationField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.duration.tooltip")));
        FieldValidator.registerLongValidator(durationField, 0L, 5000L, workingCopy::duration);
        leftScrollList.addField(Component.translatable("easegui.editor.field.duration").getString(), durationField);

        if (activeFeatures.contains(ProfileFeature.OFFSET)) {
            EditBox offsetXField = createTextField(String.valueOf(workingCopy.getInitialOffsetX()));
            offsetXField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.offset_x.tooltip")));
            FieldValidator.registerFloatValidator(offsetXField, -1000f, 1000f, workingCopy::initialOffsetX);

            EditBox offsetYField = createTextField(String.valueOf(workingCopy.getInitialOffsetY()));
            offsetYField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.offset_y.tooltip")));
            FieldValidator.registerFloatValidator(offsetYField, -1000f, 1000f, workingCopy::initialOffsetY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.offset").getString(), offsetXField, offsetYField);
        }

        if (activeFeatures.contains(ProfileFeature.SCALE)) {
            EditBox scaleXField = createTextField(String.valueOf(workingCopy.getInitialScaleX()));
            scaleXField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.scale_x.tooltip")));
            FieldValidator.registerFloatValidator(scaleXField, 0.0f, 10.0f, workingCopy::initialScaleX);

            EditBox scaleYField = createTextField(String.valueOf(workingCopy.getInitialScaleY()));
            scaleYField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.scale_y.tooltip")));
            FieldValidator.registerFloatValidator(scaleYField, 0.0f, 10.0f, workingCopy::initialScaleY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.scale").getString(), scaleXField, scaleYField);
        }

        if (activeFeatures.contains(ProfileFeature.ALPHA)) {
            EditBox alphaField = createTextField(String.valueOf(workingCopy.getInitialAlpha()));
            alphaField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.alpha.tooltip")));
            FieldValidator.registerFloatValidator(alphaField, 0.0f, 1.0f, workingCopy::initialAlpha);
            leftScrollList.addField(Component.translatable("easegui.editor.field.alpha").getString(), alphaField);
        }

        if (activeFeatures.contains(ProfileFeature.CASCADE_DELAY)) {
            EditBox cascadeField = createTextField(String.valueOf(workingCopy.getCascadeDelay()));
            cascadeField.setTooltip(Tooltip.create(Component.translatable("easegui.editor.field.cascade_delay.tooltip")));
            FieldValidator.registerLongValidator(cascadeField, 0L, 1000L, workingCopy::cascadeDelay);
            leftScrollList.addField(Component.translatable("easegui.editor.field.cascade_delay").getString(), cascadeField);
        }

        if (activeFeatures.contains(ProfileFeature.CASCADE_DIRECTION)) {
            Component dirComp = workingCopy.getCascadeDirection().getDisplayName();
            leftScrollList.addWidget(Button.builder(
                            Component.translatable("easegui.editor.button.cascade_dir", dirComp), button -> {
                                CascadeDirection[] v = CascadeDirection.values();
                                workingCopy.cascadeDirection(v[(workingCopy.getCascadeDirection().ordinal() + 1) % v.length]);
                                button.setMessage(Component.translatable("easegui.editor.button.cascade_dir", workingCopy.getCascadeDirection().getDisplayName()));
                            })
                    .tooltip(Tooltip.create(Component.translatable("easegui.editor.button.cascade_dir.tooltip")))
                    .build());
        }

        if (activeFeatures.contains(ProfileFeature.PIVOT)) {
            Component pivotComp = Component.translatable("easegui.pivot." + workingCopy.getPivot().name().toLowerCase());
            leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.pivot", pivotComp), button -> {
                        PivotPoint[] v = PivotPoint.values();
                        workingCopy.pivot(v[(workingCopy.getPivot().ordinal() + 1) % v.length]);
                        Component updatedPivot = Component.translatable("easegui.pivot." + workingCopy.getPivot().name().toLowerCase());
                        button.setMessage(Component.translatable("easegui.editor.button.pivot", updatedPivot));
                    })
                    .tooltip(Tooltip.create(Component.translatable("easegui.editor.button.pivot.tooltip")))
                    .build());
        }

        Component easingComp = Component.literal(StringUtils.toTitleCase(workingCopy.getEasing()));
        leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.easing", easingComp), button -> {
                    EasingType[] values = EasingType.values();
                    workingCopy.easing(values[(workingCopy.getEasing().ordinal() + 1) % values.length]);
                    Component updatedEasing = Component.literal(StringUtils.toTitleCase(workingCopy.getEasing()));
                    button.setMessage(Component.translatable("easegui.editor.button.easing", updatedEasing));
                })
                .tooltip(Tooltip.create(Component.translatable("easegui.editor.button.easing.tooltip")))
                .build());

        this.addRenderableWidget(leftScrollList);

        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.cancel"), _ ->
                onClose()).bounds(halfWidth - 105, this.height - 30, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.save"), _ ->
                saveAndClose()).bounds(halfWidth + 5, this.height - 30, 100, 20).build());
    }

    private EditBox createTextField(String value) {
        EditBox editBox = new EditBox(this.minecraft.font, 0, 0, 60, 16, Component.empty());
        editBox.setValue(value);
        return editBox;
    }

    private void saveAndClose() {
        this.onSave.accept(this.workingCopy);
        onClose();
    }

    @Override
    protected void renderOverlay(GuiGraphicsExtractor gg, int mouseX, int mouseY, float partialTick) {
        ProfilePreviewRenderer.render(gg, this.font, this.width, this.height, this.workingCopy, this.activeFeatures);
    }
}