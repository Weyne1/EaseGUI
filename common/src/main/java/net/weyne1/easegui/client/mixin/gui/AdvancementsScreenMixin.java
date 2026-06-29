package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
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

    @Unique
    private AnimationScope easeGUI$windowScope = null;

    // === АНИМАЦИЯ ГЛАВНОГО ОКНА ===
    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderInside(Lnet/minecraft/client/gui/GuiGraphics;IIII)V")
    )
    private void easeGUI$preRenderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
        }

        this.easeGUI$windowScope = AdvancementsAnimator.beginRenderWindow((AdvancementsScreen) (Object) this, guiGraphics);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void easeGUI$postRenderWindow(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.easeGUI$windowScope != null) {
            this.easeGUI$windowScope.close();
            this.easeGUI$windowScope = null;
        }
    }

    // === АНИМАЦИЯ ВКЛАДОК (ФОН) ===
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawTab(Lnet/minecraft/client/gui/GuiGraphics;IIZ)V")
    )
    private void easeGUI$wrapDrawTab(AdvancementTab tab, GuiGraphics guiGraphics, int offsetX, int offsetY, boolean isSelected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        AnimationScope scope = AdvancementsAnimator.beginRenderTab(screen, guiGraphics, index);
        if (scope != null) {
            try (scope) {
                original.call(tab, guiGraphics, offsetX, offsetY, isSelected);
            }
        } else {
            original.call(tab, guiGraphics, offsetX, offsetY, isSelected);
        }
    }

    // === АНИМАЦИЯ ВКЛАДОК (ИКОНКИ) ===
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawIcon(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void easeGUI$wrapDrawIcon(AdvancementTab tab, GuiGraphics guiGraphics, int offsetX, int offsetY, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        AnimationScope scope = AdvancementsAnimator.beginRenderTab(screen, guiGraphics, index);
        if (scope != null) {
            try (scope) {
                original.call(tab, guiGraphics, offsetX, offsetY);
            }
        } else {
            original.call(tab, guiGraphics, offsetX, offsetY);
        }
    }
}