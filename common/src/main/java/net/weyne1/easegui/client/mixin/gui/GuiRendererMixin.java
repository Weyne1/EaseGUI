package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiItemAtlas;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.weyne1.easegui.client.extension.ItemExtension;
import net.weyne1.easegui.client.util.ColorUtils;
import org.joml.Matrix3x2fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @WrapOperation(
            method = "submitBlitFromItemAtlas",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/BlitRenderState;")
    )
    private BlitRenderState easegui$applyStoredAlphaToItems(
            RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose,
            int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color,
            ScreenRectangle scissorArea, ScreenRectangle bounds,
            Operation<BlitRenderState> original,
            GuiItemRenderState itemState, GuiItemAtlas.SlotView slotView) {

        float alpha = ((ItemExtension) (Object) itemState).easegui$getAlpha();
        int finalColor = ColorUtils.multiplyPremultiplied(color, alpha);

        return original.call(pipeline, textureSetup, pose, x0, y0, x1, y1, u0, u1, v0, v1, finalColor, scissorArea, bounds);
    }
}