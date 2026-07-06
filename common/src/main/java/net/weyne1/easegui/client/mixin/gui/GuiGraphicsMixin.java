package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.util.ColorUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyColorFrom(int colorFrom) {
        if (!AnimationContext.isAnimating()) return colorFrom;
        return ColorUtils.getAnimatedColor(colorFrom);
    }

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Integer easeGUI$modifyColorTo(Integer colorTo) {
        if (!AnimationContext.isAnimating() || colorTo == null) return colorTo;
        return ColorUtils.getAnimatedColor(colorTo);
    }

    @ModifyVariable(method = "submitBlit", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyBlitColor(int color) {
        if (!AnimationContext.isAnimating()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @ModifyVariable(method = "submitTiledBlit", at = @At("HEAD"), argsOnly = true, ordinal = 6)
    private int easeGUI$modifyTiledBlitColor(int color) {
        if (!AnimationContext.isAnimating()) return color;
        return ColorUtils.getAnimatedColor(color);
    }
}