package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.extension.WidgetExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.AnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.api.WidgetCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetExtension {

    @Shadow protected float alpha;

    @Unique private final AnimationState easeGUI$animationState = new AnimationState();
    @Unique private WidgetCategory easeGUI$cachedCategory = null;

    @Override
    public float easeGUI$getAlpha() {
        return this.alpha;
    }

    @Override
    public WidgetCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            this.easeGUI$cachedCategory = WidgetCategory.fromClass(this.getClass());
        }
        return this.easeGUI$cachedCategory;
    }

    @WrapMethod(method = "render")
    private void easeGUI$wrapWidgetRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible) {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        var category = this.easeGUI$getCategory();
        if (category == null || category == WidgetCategory.UNKNOWN) {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }

        try (AnimationScope ignored = WidgetAnimator.beginRender(widget, guiGraphics, category, this.easeGUI$animationState)) {
            original.call(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}