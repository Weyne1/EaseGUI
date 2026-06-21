package net.weyne1.easegui.client.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.weyne1.easegui.client.animator.ListItemAnimator;
import net.weyne1.easegui.client.state.RenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin {

    @Unique
    private boolean easeGUI$isItemAnimatedInCurrentFrame = false;

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void easeGUI$onPreRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                         int index, int left, int top, int width, int height, CallbackInfo ci) {
        RenderContext.enterListEntry();

        if (ListItemAnimator.preRender(guiGraphics, top, left, width, height)) {
            this.easeGUI$isItemAnimatedInCurrentFrame = true;
        }
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void easeGUI$onPostRenderItem(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                          int index, int left, int top, int width, int height, CallbackInfo ci) {
        if (this.easeGUI$isItemAnimatedInCurrentFrame) {
            ListItemAnimator.postRender(guiGraphics);
            this.easeGUI$isItemAnimatedInCurrentFrame = false;
        }

        RenderContext.exitListEntry();
    }
}