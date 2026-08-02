package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Shadow public abstract PoseStack pose();

    @WrapOperation(
            method = "enableScissor",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/navigation/ScreenRectangle;")
    )
    private ScreenRectangle easeGUI$transformScissorBounds(int x, int y, int width, int height, Operation<ScreenRectangle> original) {
        if (!AnimationContext.isActive()) {
            return original.call(x, y, width, height);
        }

        int maxX = x + width;
        int maxY = y + height;

        Matrix4f matrix = this.pose().last().pose();
        float sx = matrix.m00();
        float sy = matrix.m11();
        float tx = matrix.m30();
        float ty = matrix.m31();

        int newMinX = Math.round(x * sx + tx);
        int newMinY = Math.round(y * sy + ty);
        int newMaxX = Math.round(maxX * sx + tx);
        int newMaxY = Math.round(maxY * sy + ty);

        int newWidth = Math.max(0, newMaxX - newMinX);
        int newHeight = Math.max(0, newMaxY - newMinY);

        return original.call(newMinX, newMinY, newWidth, newHeight);
    }
}