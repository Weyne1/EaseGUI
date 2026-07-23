package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;extractInside(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V")
    )
    private void easeGUI$preRenderWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
            AnimationContext.popParentAnimation();
        }

        this.easeGUI$windowScope = AdvancementsAnimator.beginRenderWindow((AdvancementsScreen) (Object) this, graphics);

        if (this.easeGUI$windowScope != null) {
            AnimationContext.pushParentAnimation();
        }
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void easeGUI$postRenderWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
            this.easeGUI$windowScope = null;
            AnimationContext.popParentAnimation();
        }
    }

    // Tab animation (bg)
    @WrapOperation(
            method = "extractWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;extractTab(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIZ)V")
    )
    private void easeGUI$wrapDrawTab(AdvancementTab tab, GuiGraphicsExtractor graphics, int xo, int yo, int mouseX, int mouseY, boolean selected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, graphics, index)) {
            original.call(tab, graphics, xo, yo, mouseX, mouseY, selected);
        }
    }

    // Tab animation (fg/icons)
    @WrapOperation(
            method = "extractWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;extractIcon(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
    )
    private void easeGUI$wrapDrawIcon(AdvancementTab tab, GuiGraphicsExtractor graphics, int xo, int yo, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, graphics, index)) {
            original.call(tab, graphics, xo, yo);
        }
    }
}