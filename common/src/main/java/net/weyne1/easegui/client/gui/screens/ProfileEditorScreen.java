package net.weyne1.easegui.client.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.weyne1.easegui.api.animation.AnimationProfile;
import net.weyne1.easegui.api.animation.AnimationDirection;
import net.weyne1.easegui.api.animation.CascadeDirection;
import net.weyne1.easegui.api.animation.DirectionalAnimationProfile;
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
    private final DirectionalAnimationProfile workingCopy;
    private final DirectionalAnimationProfile defaultProfile;
    private final Consumer<DirectionalAnimationProfile> onSave;
    private final EnumSet<ProfileFeature> activeFeatures;

    public ProfileEditorScreen(
            Screen parent,
            DirectionalAnimationProfile originalProfile,
            DirectionalAnimationProfile defaultProfile,
            EnumSet<ProfileFeature> features,
            Consumer<DirectionalAnimationProfile> onSave
    ) {
        super(Component.translatable("easegui.editor.title"), parent);
        this.onSave = onSave;
        this.activeFeatures = features;
        this.defaultProfile = cloneDirectionalProfile(defaultProfile);
        this.workingCopy = cloneDirectionalProfile(originalProfile);

        // Гарантируем наличие IN-профиля для редактирования
        if (this.workingCopy.getIn() == null) {
            this.workingCopy.setIn(new AnimationProfile());
        }
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

        // FIXME: Экран конфига может настраивать только IN анимацию (временное решение)
        AnimationProfile inProfile = this.workingCopy.getIn();

        // --- 0. Переключатель "Включено / Выключено" + кнопка "Reset" ---
        // FIXME: Профили IN и OUT могут быть null.
        //  Когда буду делать вкладки профилей, а не только IN:
        //  Нужно предусмотреть генерацию пустого профиля, если он null
        //  (делать обратную копию второго, если второй есть; или делать дефолт залушуку, если второго профиля тоже нет)
        Component statusComp = inProfile.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
        Button toggleBtn = Button.builder(
                Component.translatable("easegui.editor.button.enabled", statusComp),
                button -> {
                    inProfile.enabled(!inProfile.isEnabled());
                    Component newStatus = inProfile.isEnabled() ? Component.translatable("easegui.generic.on") : Component.translatable("easegui.generic.off");
                    button.setMessage(Component.translatable("easegui.editor.button.enabled", newStatus));
                }
        ).build();

        Button resetBtn = Button.builder(
                Component.translatable("easegui.generic.reset"),
                _ -> {
                    AnimationProfile defaultIn = this.defaultProfile.getIn();
                    // FIXME: тут тоже может быть null
                    if (defaultIn != null) {
                        applyProfileValues(inProfile, defaultIn);
                    } else {
                        applyProfileValues(inProfile, new AnimationProfile());
                    }
                    this.init(this.width, this.height);
                }
        ).build();

        leftScrollList.addTwoButtons(toggleBtn, resetBtn, 0.70f);

        // --- 1. Длительность (Лимит: от 0 до 5000 мс) ---
        EditBox durationField = createTextField(String.valueOf(inProfile.getDuration()));
        FieldValidator.registerLongValidator(durationField, 0L, 5000L, inProfile::duration);
        leftScrollList.addField(Component.translatable("easegui.editor.field.duration").getString(), durationField);

        // --- 2. Смещение (Лимит: от -1000 до 1000 пикселей) ---
        if (activeFeatures.contains(ProfileFeature.OFFSET)) {
            EditBox ox = createTextField(String.valueOf(inProfile.getInitialOffsetX()));
            FieldValidator.registerFloatValidator(ox, -1000f, 1000f, inProfile::initialOffsetX);

            EditBox oy = createTextField(String.valueOf(inProfile.getInitialOffsetY()));
            FieldValidator.registerFloatValidator(oy, -1000f, 1000f, inProfile::initialOffsetY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.offset").getString(), ox, oy);
        }

        // --- 3. Масштаб (Лимит: от 0.0 до 10.0 крат) ---
        if (activeFeatures.contains(ProfileFeature.SCALE)) {
            EditBox sx = createTextField(String.valueOf(inProfile.getInitialScaleX()));
            FieldValidator.registerFloatValidator(sx, 0.0f, 10.0f, inProfile::initialScaleX);

            EditBox sy = createTextField(String.valueOf(inProfile.getInitialScaleY()));
            FieldValidator.registerFloatValidator(sy, 0.0f, 10.0f, inProfile::initialScaleY);

            leftScrollList.addTwoFields(Component.translatable("easegui.editor.field.scale").getString(), sx, sy);
        }

        // --- 4. Прозрачность (Лимит: от 0.0 до 1.0) ---
        if (activeFeatures.contains(ProfileFeature.ALPHA)) {
            EditBox alphaField = createTextField(String.valueOf(inProfile.getInitialAlpha()));
            FieldValidator.registerFloatValidator(alphaField, 0.0f, 1.0f, inProfile::initialAlpha);
            leftScrollList.addField(Component.translatable("easegui.editor.field.alpha").getString(), alphaField);
        }

        // --- 5. Каскадность (Лимит: от 0 до 1000 мс) ---
        if (activeFeatures.contains(ProfileFeature.CASCADE_DELAY)) {
            EditBox cascadeField = createTextField(String.valueOf(inProfile.getCascadeDelay()));
            FieldValidator.registerLongValidator(cascadeField, 0L, 1000L, inProfile::cascadeDelay);
            leftScrollList.addField(Component.translatable("easegui.editor.field.cascade_delay").getString(), cascadeField);
        }

        // --- 5.1. Направление каскада ---
        if (activeFeatures.contains(ProfileFeature.CASCADE_DIRECTION)) {
            Component dirComp = inProfile.getCascadeDirection().getDisplayName();
            leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.cascade_dir", dirComp), b -> {
                CascadeDirection[] v = CascadeDirection.values();
                inProfile.cascadeDirection(v[(inProfile.getCascadeDirection().ordinal() + 1) % v.length]);
                b.setMessage(Component.translatable("easegui.editor.button.cascade_dir", inProfile.getCascadeDirection().getDisplayName()));
            }).build());
        }

        // --- 6. Точка опоры (Pivot) ---
        if (activeFeatures.contains(ProfileFeature.PIVOT)) {
            Component pivotComp = Component.translatable("easegui.pivot." + inProfile.getPivot().name().toLowerCase());
            leftScrollList.addWidget(Button.builder(Component.translatable("easegui.editor.button.pivot", pivotComp), b -> {
                PivotPoint[] v = PivotPoint.values();
                inProfile.pivot(v[(inProfile.getPivot().ordinal() + 1) % v.length]);
                Component updatedPivot = Component.translatable("easegui.pivot." + inProfile.getPivot().name().toLowerCase());
                b.setMessage(Component.translatable("easegui.editor.button.pivot", updatedPivot));
            }).build());
        }

        // --- 7. Интерполяция (Easing) ---
        Component easingComp = Component.literal(StringUtils.toTitleCase(inProfile.getEasing()));
        leftScrollList.addWidget(Button.builder(
                Component.translatable("easegui.editor.button.easing", easingComp),
                button -> {
                    EasingType[] values = EasingType.values();
                    inProfile.easing(values[(inProfile.getEasing().ordinal() + 1) % values.length]);
                    Component updatedEasing = Component.literal(StringUtils.toTitleCase(inProfile.getEasing()));
                    button.setMessage(Component.translatable("easegui.editor.button.easing", updatedEasing));
                }
        ).build());

        this.addRenderableWidget(leftScrollList);

        // Кнопки управления снизу
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.save"), _ -> saveAndClose()).bounds(halfWidth - 105, this.height - 30, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("easegui.generic.cancel"), _ -> onClose()).bounds(halfWidth + 5, this.height - 30, 100, 20).build());
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
    protected void renderOverlay(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        AnimationProfile inProfile = this.workingCopy.getIn();
        if (inProfile != null) {
            ProfilePreviewRenderer.render(graphics, this.font, this.width, this.height, inProfile, AnimationDirection.IN, this.activeFeatures);
        }
    }
}