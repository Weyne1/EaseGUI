package net.weyne1.easegui.client.mixin.gui;

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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    @Shadow
    private void renderTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int offsetX, int offsetY) {
        throw new AssertionError();
    }

    // Main window animation
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderInside(Lnet/minecraft/client/gui/GuiGraphics;IIII)V")
    )
    private void easeGUI$wrapWindowScope(AdvancementsScreen instance, GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, Operation<Void> original) {
        try (AnimationScope scope = AdvancementsAnimator.beginRenderWindow(instance, guiGraphics)) {
            boolean hasParent = (scope != null);
            if (hasParent) {
                AnimationContext.pushParentAnimation();
            }
            try {
                original.call(instance, guiGraphics, mouseX, mouseY, x, y);
                instance.renderWindow(guiGraphics, x, y);
                this.renderTooltips(guiGraphics, mouseX, mouseY, x, y);
            } finally {
                if (hasParent) {
                    AnimationContext.popParentAnimation();
                }
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderWindow(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void easeGUI$skipRenderWindow(AdvancementsScreen instance, GuiGraphics guiGraphics, int x, int y, Operation<Void> original) { }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementsScreen;renderTooltips(Lnet/minecraft/client/gui/GuiGraphics;IIII)V")
    )
    private void easeGUI$skipRenderTooltips(AdvancementsScreen instance, GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, Operation<Void> original) { }

    // Tab animation (bg)
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawTab(Lnet/minecraft/client/gui/GuiGraphics;IIZ)V")
    )
    private void easeGUI$wrapDrawTab(AdvancementTab tab, GuiGraphics guiGraphics, int offsetX, int offsetY, boolean isSelected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;
        int index = ((AdvancementTabAccessor) tab).easeGUI$getIndex();

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, guiGraphics, index)) {
            original.call(tab, guiGraphics, offsetX, offsetY, isSelected);
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