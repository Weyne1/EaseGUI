package net.weyne1.easegui.client.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.AnimationScope;
import net.weyne1.easegui.client.animator.AdvancementsAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(AdvancementsScreen.class)
public class AdvancementsScreenMixin {

    // Main window animation
    @WrapMethod(method = "render")
    private void easeGUI$wrapRenderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope scope = AdvancementsAnimator.beginRenderWindow(screen, graphics)) {
            boolean hasParent = (scope != null);
            if (hasParent) {
                AnimationContext.pushParentAnimation();
            }
            try {
                original.call(graphics, mouseX, mouseY, partialTick);
            } finally {
                if (hasParent) {
                    AnimationContext.popParentAnimation();
                }
            }
        }
    }

    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
    )
    private void easeGUI$ignoreSuperWidgetsInWindowScope(AdvancementsScreen instance, GuiGraphics graphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        AnimationScope currentScope = AnimationContext.getCurrentScope();

        if (currentScope != null) {
            currentScope.suspend();

            boolean hadParent = AnimationContext.hasParentAnimation();
            if (hadParent) {
                AnimationContext.popParentAnimation();
            }

            try {
                original.call(instance, graphics, mouseX, mouseY, partialTick);
            } finally {
                if (hadParent) {
                    AnimationContext.pushParentAnimation();
                }
                currentScope.resume();
            }
        } else {
            original.call(instance, graphics, mouseX, mouseY, partialTick);
        }
    }

    // Tab animation (bg)
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawTab(Lnet/minecraft/client/gui/GuiGraphics;IIIIZ)V")
    )
    private void easeGUI$wrapDrawTab(AdvancementTab tab, GuiGraphics graphics, int x, int y, int mouseX, int mouseY, boolean selected, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, graphics, tab.getIndex())) {
            original.call(tab, graphics, x, y, mouseX, mouseY, selected);
        }
    }

    // Tab animation (fg/icons)
    @WrapOperation(
            method = "renderWindow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;drawIcon(Lnet/minecraft/client/gui/GuiGraphics;II)V")
    )
    private void easeGUI$wrapDrawIcon(AdvancementTab tab, GuiGraphics graphics, int offsetX, int offsetY, Operation<Void> original) {
        AdvancementsScreen screen = (AdvancementsScreen) (Object) this;

        try (AnimationScope ignored = AdvancementsAnimator.beginRenderTab(screen, graphics, tab.getIndex())) {
            original.call(tab, graphics, offsetX, offsetY);
        }
    }
}