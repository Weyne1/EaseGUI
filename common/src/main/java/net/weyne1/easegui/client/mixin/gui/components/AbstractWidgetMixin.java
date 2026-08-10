package net.weyne1.easegui.client.mixin.gui.components;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.weyne1.easegui.client.extension.WidgetExtension;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animation.WidgetAnimationState;
import net.weyne1.easegui.client.animator.WidgetAnimator;
import net.weyne1.easegui.api.WidgetCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetExtension {

    @Unique private final WidgetAnimationState easeGUI$animationState = new WidgetAnimationState();
    @Unique private WidgetCategory easeGUI$cachedCategory = null;

    @Override
    public WidgetCategory easeGUI$getCategory() {
        if (this.easeGUI$cachedCategory == null) {
            this.easeGUI$cachedCategory = WidgetCategory.fromClass(this.getClass());
        }
        return this.easeGUI$cachedCategory;
    }

    @WrapMethod(method = "render")
    private void easeGUI$wrapWidgetRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        AbstractWidget widget = (AbstractWidget) (Object) this;

        if (!widget.visible) {
            original.call(graphics, mouseX, mouseY, partialTick);
            return;
        }

        var category = this.easeGUI$getCategory();
        if (category == null || category == WidgetCategory.UNKNOWN) {
            original.call(graphics, mouseX, mouseY, partialTick);
            return;
        }

        try (AnimationScope ignored = WidgetAnimator.beginRender(widget, graphics, category, this.easeGUI$animationState)) {
            original.call(graphics, mouseX, mouseY, partialTick);
        }
    }
}