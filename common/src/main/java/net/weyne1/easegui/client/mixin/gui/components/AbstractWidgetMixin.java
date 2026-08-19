package net.weyne1.easegui.client.mixin.gui.components;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.extension.WidgetExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.WidgetAnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.api.WidgetCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetExtension {

    @Unique private final WidgetAnimationState easeGUI$animationState = new WidgetAnimationState();
    @Unique private WidgetCategory easeGUI$cachedCategory = null;
    @Unique private boolean easeGUI$excluded = false;

    @Override
    public WidgetCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            this.easeGUI$cachedCategory = WidgetCategory.fromClass(this.getClass());
        }
        return this.easeGUI$cachedCategory;
    }

    @Override
    public void easeGUI$setExcluded(boolean excluded) {
        this.easeGUI$excluded = excluded;
    }

    @Override
    public boolean easeGUI$isExcluded() {
        return this.easeGUI$excluded;
    }

    @WrapMethod(method = "extractRenderState")
    private void easeGUI$wrapWidgetRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible || AnimationContext.isAnimationDisabled() || this.easeGUI$excluded) {
            original.call(graphics, mouseX, mouseY, a);
            return;
        }

        WidgetCategory category = this.easeGUI$getCategory();
        if (category == WidgetCategory.UNKNOWN || category == WidgetCategory.EXCLUDED) {
            original.call(graphics, mouseX, mouseY, a);
            return;
        }

        try (AnimationScope ignored = WidgetAnimator.beginWidget(widget, graphics, category, this.easeGUI$animationState)) {
            original.call(graphics, mouseX, mouseY, a);
        }
    }
}