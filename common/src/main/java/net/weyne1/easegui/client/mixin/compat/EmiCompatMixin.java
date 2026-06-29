package net.weyne1.easegui.client.mixin.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.weyne1.easegui.client.accessor.ScreenAnimationAccessor;
import net.weyne1.easegui.client.animation.AnimationScope;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Isolates EMI overlay rendering from container transformations.
 * Prevents item panels, search bars, and recipes from scaling/moving with the container.
 */
@Pseudo
@Mixin(targets = "dev.emi.emi.screen.EmiScreenManager", remap = false)
public class EmiCompatMixin {

    @Unique
    private static AnimationScope easeGUI$getCurrentScope() {
        Screen currentScreen = Minecraft.getInstance().screen;
        if (currentScreen instanceof ScreenAnimationAccessor accessor) {
            return accessor.easeGUI$getContainerScope();
        }
        return null;
    }

    @Inject(method = {"render", "drawBackground", "drawForeground"}, at = @At("HEAD"), require = 0)
    private static void easeGUI$onEmiRenderStart(CallbackInfo ci) {
        AnimationScope scope = easeGUI$getCurrentScope();
        if (scope != null) {
            scope.suspend();
        }
    }

    @Inject(method = {"render", "drawBackground", "drawForeground"}, at = @At("TAIL"), require = 0)
    private static void easeGUI$onEmiRenderEnd(CallbackInfo ci) {
        AnimationScope scope = easeGUI$getCurrentScope();
        if (scope != null) scope.resume();
    }
}