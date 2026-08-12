package net.weyne1.easegui.client.mixin.gui.components;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            method = "extractListItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;extractItem(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIFLnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)V"
            )
    )
    private void easeGUI$wrapRenderItem(AbstractSelectionList<?> instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, @Coerce Object entry, Operation<Void> original) {
        LayoutElement element = (LayoutElement) entry;
        int left = this.getRowLeft();
        int width = this.getRowWidth();
        int top = element.getY();
        int height = element.getHeight();

        try (AnimationScope ignored = ListItemAnimator.beginListItems(graphics, top, left, width, height)) {
            AnimationContext.pushParentAnimation();
            try {
                original.call(instance, graphics, mouseX, mouseY, a, entry);
            } finally {
                AnimationContext.popParentAnimation();
            }
        }
    }
}