package net.weyne1.easegui.client.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.*;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.animation.PipTransform;
import net.weyne1.easegui.client.util.ColorUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@SuppressWarnings("ModifyVariableMayUseName")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyColorFrom(int colorFrom) {
        if (!AnimationContext.isActive()) return colorFrom;
        return ColorUtils.getAnimatedColor(colorFrom);
    }

    @ModifyVariable(method = "submitColoredRectangle", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Integer easeGUI$modifyColorTo(Integer colorTo) {
        if (!AnimationContext.isActive() || colorTo == null) return colorTo;
        return ColorUtils.getAnimatedColor(colorTo);
    }

    @ModifyVariable(method = "submitBlit", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private int easeGUI$modifyBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @ModifyVariable(method = "submitTiledBlit", at = @At("HEAD"), argsOnly = true, ordinal = 6)
    private int easeGUI$modifyTiledBlitColor(int color) {
        if (!AnimationContext.isActive()) return color;
        return ColorUtils.getAnimatedColor(color);
    }

    @WrapOperation(
            method = "submitEntityRenderState",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;")
    )
    private GuiEntityRenderState easeGUI$transformEntity(
            EntityRenderState renderState, Vector3f translation, Quaternionf rotation, Quaternionf overrideCameraAngle,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea,
            Operation<GuiEntityRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                renderState, translation, rotation, overrideCameraAngle,
                xs[0], ys[0], xs[1], ys[1], PipTransform.scale(scale), scissorArea
        );
    }

    @WrapOperation(
            method = "submitBookModelRenderState",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/pip/GuiBookModelRenderState;")
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
            method = "submitSkinRenderState",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/pip/GuiSkinRenderState;")
    )
    private GuiSkinRenderState easeGUI$transformSkin(
            PlayerModel playerModel, Identifier texture, float rotationX, float rotationY, float pivotY,
            int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea, Operation<GuiSkinRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                playerModel, texture, rotationY, pivotY,
                (float) xs[0], ys[0], xs[1], ys[1], Math.round(PipTransform.scale(scale)), rotationX, scissorArea
        );
    }

    @WrapOperation(
            method = "submitBannerPatternRenderState",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/pip/GuiBannerResultRenderState;")
    )
    private GuiBannerResultRenderState easeGUI$transformBanner(
            BannerFlagModel flag, DyeColor baseColor, BannerPatternLayers resultBannerPatterns,
            int x0, int y0, int x1, int y1, ScreenRectangle scissorArea,
            Operation<GuiBannerResultRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(flag, baseColor, resultBannerPatterns, xs[0], ys[0], xs[1], ys[1], scissorArea);
    }

    @WrapOperation(
            method = "submitSignRenderState",
            at = @At(value = "NEW", target = "Lnet/minecraft/client/gui/render/state/pip/GuiSignRenderState;")
    )
    private GuiSignRenderState easeGUI$transformSign(
            Model.Simple signModel, WoodType woodType, int x0, int y0, int x1, int y1, float scale, ScreenRectangle scissorArea,
            Operation<GuiSignRenderState> original) {

        int[] xs = PipTransform.transformRangeX(x0, x1);
        int[] ys = PipTransform.transformRangeY(y0, y1);

        return original.call(
                signModel, woodType,
                xs[0], ys[0], xs[1], ys[1],
                PipTransform.scale(scale), scissorArea
        );
    }
}