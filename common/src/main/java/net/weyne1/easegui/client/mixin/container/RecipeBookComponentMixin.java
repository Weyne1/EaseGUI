package net.weyne1.easegui.client.mixin.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.weyne1.easegui.client.animator.ContainerAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Синхронизирует матрицу Книги Рецептов с анимацией родительского контейнера.
 */
@Mixin(RecipeBookComponent.class)
public class RecipeBookComponentMixin {

    @Unique private boolean easeGUI$isBookAnimatedInCurrentFrame = false;
    @Unique private boolean easeGUI$isGhostAnimatedInCurrentFrame = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void easeGUI$onPreRenderBook(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        if (easeGUI$executeApply(gg)) {
            this.easeGUI$isBookAnimatedInCurrentFrame = true;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$onPostRenderBook(GuiGraphics gg, int mx, int my, float pt, CallbackInfo ci) {
        if (this.easeGUI$isBookAnimatedInCurrentFrame) {
            ContainerAnimator.postRender(gg);
            this.easeGUI$isBookAnimatedInCurrentFrame = false;
        }
    }

    @Inject(method = "renderGhostRecipe", at = @At("HEAD"))
    private void easeGUI$onPreRenderGhost(GuiGraphics gg, int left, int top, boolean slots, float pt, CallbackInfo ci) {
        if (easeGUI$executeApply(gg)) {
            this.easeGUI$isGhostAnimatedInCurrentFrame = true;
        }
    }

    @Inject(method = "renderGhostRecipe", at = @At("RETURN"))
    private void easeGUI$onPostRenderGhost(GuiGraphics gg, int left, int top, boolean slots, float pt, CallbackInfo ci) {
        if (this.easeGUI$isGhostAnimatedInCurrentFrame) {
            ContainerAnimator.postRender(gg);
            this.easeGUI$isGhostAnimatedInCurrentFrame = false;
        }
    }

    @Unique
    private boolean easeGUI$executeApply(GuiGraphics gg) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
            return ContainerAnimator.preRender(screen, gg);
        }
        return false;
    }
}