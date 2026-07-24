package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.extension.WidgetExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.client.config.ConfigManager;
import net.weyne1.easegui.api.WidgetCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetExtension {

    @Shadow protected boolean isHovered;

    @Unique private final AnimationState easeGUI$animationState = new AnimationState();
    @Unique private WidgetCategory easeGUI$cachedCategory = null;

    @Override
    public WidgetCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            this.easeGUI$cachedCategory = WidgetCategory.fromClass(this.getClass());
        }
        return this.easeGUI$cachedCategory;
    }

    @WrapMethod(method = "extractRenderState")
    private void easeGUI$wrapWidgetRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible) {
            original.call(graphics, mouseX, mouseY, a);
            return;
        }

        var category = this.easeGUI$getCategory();
        if (category == null || category == WidgetCategory.UNKNOWN) {
            original.call(graphics, mouseX, mouseY, a);
            return;
        }

        try (AnimationScope ignored = WidgetAnimator.beginRender(widget, graphics, category, this.easeGUI$animationState)) {

            var profile = ConfigManager.getProfileForCurrentContext(category);
            boolean shouldBypassHover = false;

            if (profile != null && profile.isEnabled() && this.easeGUI$animationState.init) {
                long elapsed = Util.getMillis() - this.easeGUI$animationState.startTime - this.easeGUI$animationState.delay;
                if (elapsed < profile.getDuration()) {
                    shouldBypassHover = true;
                }
            }

            if (shouldBypassHover) {
                boolean savedHover = this.isHovered;
                this.isHovered = false;

                original.call(graphics, mouseX, mouseY, a);

                this.isHovered = savedHover;
            } else {
                original.call(graphics, mouseX, mouseY, a);
            }
        }
    }
}