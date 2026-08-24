package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiBannerResultRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiBookModelRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiSkinRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.PipTransform;
import net.weyne1.easegui.client.util.ColorUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {


    @ModifyVariable(method = "innerFill", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easegui$modifyFillColor(int color1) {
        if (!AnimationContext.isActive()) return color1;
        return ColorUtils.getAnimatedColor(color1);
    }

    @ModifyVariable(
            method = "innerBlit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 4
    )
    private int easegui$modifyBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @ModifyVariable(method = "innerTiledBlit", at = @At("HEAD"), argsOnly = true, ordinal = 6)
    private int easegui$modifyTiledBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @WrapOperation(
            method = "entity",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiEntityRenderState;")
    )
    private GuiEntityRenderState easegui$transformEntity(
            EntityRenderState renderState, Vector3f translation, Quaternionf rotation, Quaternionf overrideCameraAngle,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, Operation<GuiEntityRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                renderState, translation, rotation, overrideCameraAngle,
                xs[0], ys[0], xs[1], ys[1], PipTransform.scale(scale), scissorArea
        );
    }

    @WrapOperation(
            method = "book",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiBookModelRenderState;")
    )
    private GuiBookModelRenderState easegui$transformBook(
            BookModel bookModel, Identifier texture, float _open, float flip,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, Operation<GuiBookModelRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                bookModel, texture, _open, flip,
                xs[0], ys[0], xs[1], ys[1], PipTransform.scale(scale), scissorArea
        );
    }

    @WrapOperation(
            method = "skin",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiSkinRenderState;")
    )
    private GuiSkinRenderState easegui$transformSkin(
            PlayerModel playerModel, Identifier texture, float rotationX, float rotationY, float pivotY,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, Operation<GuiSkinRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                playerModel, texture, rotationX, rotationY, pivotY,
                xs[0], ys[0], xs[1], ys[1], PipTransform.scale(scale), scissorArea
        );
    }

    @WrapOperation(
            method = "bannerPattern",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiBannerResultRenderState;")
    )
    private GuiBannerResultRenderState easegui$transformBanner(
            BannerFlagModel flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns,
            int x0, int y0, int x1, int y1, ScreenRectangle scissorArea,
            Operation<GuiBannerResultRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(flag, baseColor, resultBannerPatterns, xs[0], ys[0], xs[1], ys[1], scissorArea);
    }
}