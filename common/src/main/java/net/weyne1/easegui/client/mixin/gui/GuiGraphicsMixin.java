package net.weyne1.easegui.client.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Shadow public abstract PoseStack pose();

    @ModifyArgs(
            method = "enableScissor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/navigation/ScreenRectangle;<init>(IIII)V")
    )
    private void easeGUI$transformScissorBounds(Args args) {
        if (!AnimationContext.isAnimating()) {
            return;
        }

        int minX = args.get(0);
        int minY = args.get(1);
        int width = args.get(2);
        int height = args.get(3);
        int maxX = minX + width;
        int maxY = minY + height;

        Matrix4f matrix = this.pose().last().pose();
        float sx = matrix.m00();
        float sy = matrix.m11();
        float tx = matrix.m30();
        float ty = matrix.m31();

        int newMinX = Math.round(minX * sx + tx);
        int newMinY = Math.round(minY * sy + ty);
        int newMaxX = Math.round(maxX * sx + tx);
        int newMaxY = Math.round(maxY * sy + ty);

        int newWidth = newMaxX - newMinX;
        int newHeight = newMaxY - newMinY;

        args.set(0, newMinX);
        args.set(1, newMinY);
        args.set(2, Math.max(0, newWidth));
        args.set(3, Math.max(0, newHeight));
    }
}