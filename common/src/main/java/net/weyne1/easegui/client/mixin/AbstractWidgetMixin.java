package net.weyne1.easegui.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.weyne1.easegui.client.state.EaseGUIState;
import net.weyne1.easegui.client.EaseGUIWidget;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.client.config.UIElementCategory;
import net.weyne1.easegui.client.state.RenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements EaseGUIWidget {

    @Shadow protected float alpha;

    @Unique private final EaseGUIState.AnimationData easeGUI$animationData = new EaseGUIState.AnimationData();
    @Unique private UIElementCategory easeGUI$cachedCategory = null;
    @Unique private boolean easeGUI$isAnimatingInCurrentFrame = false;

    @Override
    public float easeGUI$getAlpha() {
        return this.alpha;
    }

    @Override
    public UIElementCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            Class<?> clazz = this.getClass();
            if (AbstractButton.class.isAssignableFrom(clazz) || AbstractSliderButton.class.isAssignableFrom(clazz) || EditBox.class.isAssignableFrom(clazz)) {
                this.easeGUI$cachedCategory = UIElementCategory.BUTTON_LIKE;
            }
            else if (AbstractSelectionList.class.isAssignableFrom(clazz)) {
                this.easeGUI$cachedCategory = UIElementCategory.SCROLLABLE;
            }
            else if (StringWidget.class.isAssignableFrom(clazz) || MultiLineTextWidget.class.isAssignableFrom(clazz)) {
                this.easeGUI$cachedCategory = UIElementCategory.TEXT;
            }
            else {
                this.easeGUI$cachedCategory = UIElementCategory.UNKNOWN;
            }
        }
        return this.easeGUI$cachedCategory;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void easeGUI$onPreRender(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        // Фильтрация контекстов, где индивидуальная анимация виджета должна быть проигнорирована
        if (!widget.visible || RenderContext.isInsideListEntry() || Minecraft.getInstance().screen instanceof AbstractContainerScreen) {
            return;
        }

        var category = this.easeGUI$getCategory();
        if (category == null || category == UIElementCategory.UNKNOWN) return;

        if (WidgetAnimator.preRender(widget, gg, category, this.easeGUI$animationData)) {
            this.easeGUI$isAnimatingInCurrentFrame = true;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$onPostRender(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        if (this.easeGUI$isAnimatingInCurrentFrame) {
            WidgetAnimator.postRender(gg);
            this.easeGUI$isAnimatingInCurrentFrame = false;
        }
    }
}