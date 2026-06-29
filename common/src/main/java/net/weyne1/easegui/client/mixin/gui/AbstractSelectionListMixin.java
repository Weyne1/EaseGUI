package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @Unique
    private AnimationScope easeGUI$itemScope = null;

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void easeGUI$onPreRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                         int index, int left, int top, int width, int height, CallbackInfo ci) {
        AnimationContext.pushParentAnimation();

        if (this.easeGUI$itemScope != null) {
            this.easeGUI$itemScope.close();
        }

        this.easeGUI$itemScope = ListItemAnimator.beginRender(guiGraphics, top, left, width, height);
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void easeGUI$onPostRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                          int index, int left, int top, int width, int height, CallbackInfo ci) {
        if (this.easeGUI$itemScope != null) {
            this.easeGUI$itemScope.close();
            this.easeGUI$itemScope = null;
        }

        AnimationContext.popParentAnimation();
    }
}