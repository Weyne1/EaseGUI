package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.EaseGUIWidget;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.client.config.UIElementCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements EaseGUIWidget {

    @Shadow protected float alpha;
    @Shadow protected boolean isHovered;

    @Unique private final AnimationState easeGUI$animationState = new AnimationState();
    @Unique private UIElementCategory easeGUI$cachedCategory = null;
    @Unique private AnimationScope easeGUI$widgetScope = null;

    @Override
    public float easeGUI$getAlpha() {
        return this.alpha;
    }

    @Override
    public UIElementCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            this.easeGUI$cachedCategory = UIElementCategory.fromClass(this.getClass());
        }
        return this.easeGUI$cachedCategory;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void easeGUI$onPreRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$widgetScope != null) {
            this.easeGUI$widgetScope.close();
            this.easeGUI$widgetScope = null;
        }

        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible) {
            return;
        }

        var category = this.easeGUI$getCategory();
        if (category == null || category == UIElementCategory.UNKNOWN) return;

        this.easeGUI$widgetScope = WidgetAnimator.beginRender(widget, guiGraphics, category, this.easeGUI$animationState);

        var profile = ConfigManager.getProfileForCurrentContext(category);
        if (profile != null && profile.enabled && this.easeGUI$animationState.init) {
            long elapsed = Util.getMillis() - this.easeGUI$animationState.startTime - this.easeGUI$animationState.delay;
            if (elapsed < profile.duration) {
                this.isHovered = false;
            }
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$onPostRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$widgetScope != null) {
            this.easeGUI$widgetScope.close();
            this.easeGUI$widgetScope = null;
        }
    }
}