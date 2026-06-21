package net.weyne1.easegui.client.mixin.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.weyne1.easegui.client.animator.AdvancementsAnimator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {

    @Shadow @Final private int index;

    @Unique private boolean easeGUI$isTabBackgroundAnimated = false;
    @Unique private boolean easeGUI$isTabIconAnimated = false;

    // === Фаза 1: Текстура вкладки ===
    @Inject(method = "drawTab", at = @At("HEAD"))
    private void easeGUI$preDrawTab(GuiGraphics gg, int offsetX, int offsetY, boolean isSelected, CallbackInfo ci) {
        net.minecraft.client.gui.screens.Screen currentScreen = net.minecraft.client.Minecraft.getInstance().screen;

        if (currentScreen != null && AdvancementsAnimator.preRenderTab(currentScreen, gg, this.index)) {
            this.easeGUI$isTabBackgroundAnimated = true;
        }
    }

    @Inject(method = "drawTab", at = @At("RETURN"))
    private void easeGUI$postDrawTab(GuiGraphics gg, int offsetX, int offsetY, boolean isSelected, CallbackInfo ci) {
        if (this.easeGUI$isTabBackgroundAnimated) {
            AdvancementsAnimator.postRenderTab(gg);
            this.easeGUI$isTabBackgroundAnimated = false;
        }
    }

    // === Фаза 2: Предмет-иконка поверх вкладки ===
    @Inject(method = "drawIcon", at = @At("HEAD"))
    private void easeGUI$preDrawIcon(GuiGraphics gg, int offsetX, int offsetY, CallbackInfo ci) {
        net.minecraft.client.gui.screens.Screen currentScreen = net.minecraft.client.Minecraft.getInstance().screen;

        if (currentScreen != null && AdvancementsAnimator.preRenderTab(currentScreen, gg, this.index)) {
            this.easeGUI$isTabIconAnimated = true;
        }
    }

    @Inject(method = "drawIcon", at = @At("RETURN"))
    private void easeGUI$postDrawIcon(GuiGraphics gg, int offsetX, int offsetY, CallbackInfo ci) {
        if (this.easeGUI$isTabIconAnimated) {
            AdvancementsAnimator.postRenderTab(gg);
            this.easeGUI$isTabIconAnimated = false;
        }
    }
}