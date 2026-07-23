package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
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
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {

    @ModifyVariable(method = "fillGradient", at = @At("HEAD"), argsOnly = true, name = "col1")
    private int easeGUI$modifyColorFrom(int col1) {
        if (!AnimationContext.isActive()) return col1;
        return ColorUtils.getAnimatedColor(col1);
    }

    @ModifyVariable(method = "fillGradient", at = @At("HEAD"), argsOnly = true, name = "col2")
    private int easeGUI$modifyColorTo(int col2) {
        if (!AnimationContext.isActive()) return col2;
        return ColorUtils.getAnimatedColor(col2);
    }

    @ModifyVariable(method = "innerFill", at = @At("HEAD"), argsOnly = true, name = "color1")
    private int easeGUI$modifyFillColor(int color1) {
        if (!AnimationContext.isActive()) return color1;
        return ColorUtils.getAnimatedColor(color1);
    }

    @ModifyVariable(method = "innerBlit*", at = @At("HEAD"), argsOnly = true, name = "color")
    private int easeGUI$modifyBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @ModifyVariable(method = "innerTiledBlit", at = @At("HEAD"), argsOnly = true, name = "color")
    private int easeGUI$modifyTiledBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @WrapOperation(
            method = "entity",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiEntityRenderState;")
    )
    private GuiEntityRenderState easeGUI$transformEntity(
            EntityRenderState renderState, Vector3fc translation, Quaternionfc rotation, Quaternionfc overrideCameraAngle,
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
    private GuiBookModelRenderState easeGUI$transformBook(
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
    private GuiSkinRenderState easeGUI$transformSkin(
            Model.Simple playerModel, Identifier texture, float rotationX, float rotationY, float pivotY,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, Operation<GuiSkinRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                playerModel, texture, rotationY, pivotY,
                (float) xs[0], ys[0], xs[1], ys[1], Math.round(PipTransform.scale(scale)), rotationX, scissorArea
        );
    }

    @WrapOperation(
            method = "bannerPattern",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/state/gui/pip/GuiBannerResultRenderState;")
    )
    private GuiBannerResultRenderState easeGUI$transformBanner(
            BannerFlagModel flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns,
            int x0, int y0, int x1, int y1, ScreenRectangle scissorArea,
            Operation<GuiBannerResultRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(flag, baseColor, resultBannerPatterns, xs[0], ys[0], xs[1], ys[1], scissorArea);
    }
}