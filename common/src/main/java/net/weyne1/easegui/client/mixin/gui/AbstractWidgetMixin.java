package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.extension.WidgetExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.WidgetAnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.api.WidgetCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetExtension {

    @Shadow protected float alpha;

    @Unique
    private final WidgetAnimationState easegui$animationState = new WidgetAnimationState();
    @Unique
    private WidgetCategory easegui$cachedCategory = null;
    @Unique
    private boolean easegui$excluded = false;

    @Override
    public float easegui$getAlpha() {
        return this.alpha;
    }

    @Override
    public WidgetCategory easegui$getCategory() {
        if (this.easegui$cachedCategory == null) {
            this.easegui$cachedCategory = WidgetCategory.fromClass(this.getClass());
        }
        return this.easegui$cachedCategory;
    }

    @Override
    public void easegui$setExcluded(boolean excluded) {
        this.easegui$excluded = excluded;
    }

    @Override
    public boolean easegui$isExcluded() {
        return this.easegui$excluded;
    }

    @WrapMethod(method = "render")
    private void easegui$wrapWidgetRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible || AnimationContext.isAnimationDisabled() || this.easegui$excluded) {
            original.call(graphics, mouseX, mouseY, partialTick);
            return;
        }

        WidgetCategory category = this.easegui$getCategory();
        if (category == WidgetCategory.UNKNOWN || category == WidgetCategory.EXCLUDED) {
            original.call(graphics, mouseX, mouseY, partialTick);
            return;
        }

        try (AnimationScope ignored = WidgetAnimator.beginWidget(widget, graphics, category, this.easegui$animationState)) {
            original.call(graphics, mouseX, mouseY, partialTick);
        }
    }
}