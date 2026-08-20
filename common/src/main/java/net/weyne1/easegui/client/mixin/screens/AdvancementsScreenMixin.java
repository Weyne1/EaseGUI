package net.weyne1.easegui.client.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.AdvancementsAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    // Main window animation
    @WrapMethod(method = "extractRenderState")
    private void easegui$wrapRenderWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope scope = AdvancementsAnimator.beginWindow(screen, graphics)) {
            if (scope.isAnimating()) {
                AnimationContext.pushParentAnimation();
            }

            try {
                original.call(graphics, mouseX, mouseY, a);
            } finally {
                if (scope.isAnimating()) {
                    AnimationContext.popParentAnimation();
                }
            }
        }
    }

    @WrapOperation(
            method = "extractRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V")
    )
    private void easegui$ignoreSuperWidgetsInWindowScope(AdvancementsScreen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();

        if (currentScope.isAnimating()) {
            currentScope.suspend();

            boolean hadParent = AnimationContext.hasParentAnimation();
            if (hadParent) {
                AnimationContext.popParentAnimation();
            }

            try {
                original.call(instance, graphics, mouseX, mouseY, a);
            } finally {
                if (hadParent) {
                    AnimationContext.pushParentAnimation();
                }
                currentScope.resume();
            }
        } else {
            original.call(instance, graphics, mouseX, mouseY, a);
        }
    }

    // Tab animation (bg)
    @WrapOperation(
            method = "extractWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;extractTab(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIZ)V")
    )
    private void easegui$wrapDrawTab(AdvancementTab tab, GuiGraphicsExtractor graphics, int xo, int yo, int mouseX, int mouseY, boolean selected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope ignored = AdvancementsAnimator.beginTab(screen, graphics, tab.getIndex())) {
            original.call(tab, graphics, xo, yo, mouseX, mouseY, selected);
        }
    }

    // Tab animation (fg/icons)
    @WrapOperation(
            method = "extractWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;extractIcon(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V")
    )
    private void easegui$wrapDrawIcon(AdvancementTab tab, GuiGraphicsExtractor graphics, int xo, int yo, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope ignored = AdvancementsAnimator.beginTab(screen, graphics, tab.getIndex())) {
            original.call(tab, graphics, xo, yo);
        }
    }
}