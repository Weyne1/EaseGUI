package net.weyne1.easegui.client.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.AdvancementsAnimator;
import net.weyne1.easegui.client.mixin.accessor.AdvancementTabAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    @Unique private AnimationScope easeGUI$windowScope = null;

    // Main window animation
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderInside(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void easeGUI$preRenderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
            AnimationContext.popParentAnimation();
        }

        this.easeGUI$windowScope = AdvancementsAnimator.beginRenderWindow((AdvancementsScreen) (Object) this, guiGraphics);

        if (this.easeGUI$windowScope != null) {
            AnimationContext.pushParentAnimation();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$postRenderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
            this.easeGUI$windowScope = null;
            AnimationContext.popParentAnimation();
        }
    }

    // Tab animation (bg)
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawTab(Lnet/minecraft/client/gui/GuiGraphics;IIIIZ)V")
    )
    private void easeGUI$wrapDrawTab(AdvancementTab tab, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, boolean selected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, guiGraphics, index)) {
            original.call(tab, guiGraphics, x, y, mouseX, mouseY, selected);
        }
    }

    // Tab animation (fg/icons)
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawIcon(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void easeGUI$wrapDrawIcon(AdvancementTab tab, GuiGraphics guiGraphics, int offsetX, int offsetY, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, guiGraphics, index)) {
            original.call(tab, guiGraphics, offsetX, offsetY);
        }
    }
}