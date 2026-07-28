package net.weyne1.easegui.client.mixin.renderer.state.gui;

import net.minecraft.client.renderer.state.gui.GuiTextRenderState;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, name = "color")
    private static int easeGUI$modifyTextStateColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, name = "backgroundColor")
    private static int easeGUI$modifyTextStateBackgroundColor(int backgroundColor) {
        if (!AnimationContext.isActive() || backgroundColor == 0) return backgroundColor;
        return ColorUtils.getAnimatedColor(backgroundColor);
    }
}