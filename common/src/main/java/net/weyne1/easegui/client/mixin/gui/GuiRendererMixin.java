package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.weyne1.easegui.client.extension.EaseGuiItemExtension;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @SuppressWarnings("NameDoesntMatchTargetClass")
    @WrapOperation(
            method = "submitBlitFromItemAtlas",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/BlitRenderState;")
    )
    private BlitRenderState easeGUI$applyStoredAlphaToItems(
            RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose,
            int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color,
            ScreenRectangle scissorArea, ScreenRectangle bounds,
            Operation<BlitRenderState> original,
            GuiItemRenderState renderState, float x, float y, int itemSize, int atlasSize) {
        float alpha = ((EaseGuiItemExtension) (Object) renderState).easegui$getAlpha();

        if (alpha >= 1.0f) {
            return original.call(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, color, scissorArea, bounds);
        }

        int a = (int) (alpha * 255) & 0xFF;

        int premultipliedColor = (a << 24) | (a << 16) | (a << 8) | a;

        return original.call(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, premultipliedColor, scissorArea, bounds);
    }
}