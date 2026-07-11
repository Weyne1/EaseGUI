package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @WrapMethod(method = "renderItem")
    private void easeGUI$wrapRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                        int index, int left, int top, int width, int height, Operation<Void> original) {
        try (AnimationScope ignored = ListItemAnimator.beginRender(guiGraphics, top, left, width, height)) {
            AnimationContext.pushParentAnimation();
            try {
                original.call(guiGraphics, mouseX, mouseY, partialTick, index, left, top, width, height);
            } finally {
                AnimationContext.popParentAnimation();
            }
        }
    }
}