package net.weyne1.easegui.client.mixin.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyColorFrom(int colorFrom) {
        if (!AnimationContext.isAnimating()) return colorFrom;
        return easegui$getNewColor(colorFrom);
    }

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Integer easeGUI$modifyColorTo(Integer colorTo) {
        if (!AnimationContext.isAnimating() || colorTo == null) return colorTo;
        return easegui$getNewColor(colorTo);
    }

    @ModifyVariable(method = "submitBlit", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyBlitColor(int color) {
        if (!AnimationContext.isAnimating()) return color;
        return easegui$getNewColor(color);
    }

    @ModifyVariable(method = "submitTiledBlit", at = @At("HEAD"), argsOnly = true, ordinal = 6)
    private int easeGUI$modifyTiledBlitColor(int color) {
        if (!AnimationContext.isAnimating()) return color;
        return easegui$getNewColor(color);
    }

    @Unique
    private static int easegui$getNewColor(int originalColor) {
        int originalAlpha = (originalColor >> 24) & 0xFF;

        if (originalAlpha == 0 && (originalColor & 0x00FFFFFF) != 0) {
            originalAlpha = 255;
        }

        float animationAlpha = AnimationContext.getCurrentAlpha();
        int finalAlpha = Math.round(originalAlpha * animationAlpha);

        return (originalColor & 0x00FFFFFF) | (finalAlpha << 24);
    }
}