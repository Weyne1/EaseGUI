package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {

    @Shadow public abstract int getRowLeft();
    @Shadow public abstract int getRowWidth();

    @WrapOperation(
            method = "renderListItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;renderItem(Lnet/minecraft/client/gui/GuiGraphics;IIFLnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)V"
            )
    )
    private void easeGUI$wrapRenderItem(AbstractSelectionList<?> instance, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, @Coerce Object item, Operation<Void> original) {
        AnimationContext.pushParentAnimation();

        LayoutElement element = (LayoutElement) item;
        int left = this.getRowLeft();
        int width = this.getRowWidth();
        int top = element.getY();
        int height = element.getHeight();

        AnimationScope scope = ListItemAnimator.beginRender(guiGraphics, top, left, width, height);

        if (scope != null) {
            try (scope) {
                AnimationContext.pushParentAnimation();
                try {
                    original.call(instance, guiGraphics, mouseX, mouseY, partialTick, item);
                } finally {
                    if (AnimationContext.hasParentAnimation()) {
                        AnimationContext.popParentAnimation();
                    }
                }
            }
        } else {
            original.call(instance, guiGraphics, mouseX, mouseY, partialTick, item);
        }
    }
}