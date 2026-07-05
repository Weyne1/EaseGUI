package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.EaseGUIDebug;
import org.joml.Matrix3x2fStack;

public final class AnimationScope implements AutoCloseable {
    private static final float MIN_SCALE = 0.001f;

    private final GuiGraphics guiGraphics;

    private boolean isClosed = false;
    private boolean isSuspended = false;

    private float offsetX, offsetY;
    private float scaleX = 1.0f, scaleY = 1.0f;
    private float pivotX, pivotY;

    public AnimationScope(GuiGraphics guiGraphics, float alpha) {
        this.guiGraphics = guiGraphics;
        AnimationContext.pushAnimation(alpha);
        this.guiGraphics.pose().pushMatrix();
    }

    public void setTransformParams(float offsetX, float offsetY, float scaleX, float scaleY, float pivotX, float pivotY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = clampScale(scaleX);
        this.scaleY = clampScale(scaleY);
        this.pivotX = pivotX;
        this.pivotY = pivotY;

        Matrix3x2fStack poseStack = guiGraphics.pose();

        if (this.scaleX != 1.0f || this.scaleY != 1.0f) {
            poseStack.translate(offsetX + pivotX, offsetY + pivotY);
            poseStack.scale(this.scaleX, this.scaleY);
            poseStack.translate(-pivotX, -pivotY);
        } else {
            poseStack.translate(offsetX, offsetY);
        }
    }

    public void suspend() {
        if (isClosed || isSuspended) return;

        AnimationContext.suspend();
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();

        if (scaleX != 1.0f || scaleY != 1.0f) {
            poseStack.translate(pivotX, pivotY);
            poseStack.scale(1.0f / scaleX, 1.0f / scaleY);
            poseStack.translate(-(offsetX + pivotX), -(offsetY + pivotY));
        } else {
            poseStack.translate(-offsetX, -offsetY);
        }

        isSuspended = true;
    }

    public void resume() {
        if (isClosed || !isSuspended) return;

        guiGraphics.pose().popMatrix();
        AnimationContext.resume();

        isSuspended = false;
    }

    @Override
    public void close() {
        if (isClosed) return;
        isClosed = true;

        if (isSuspended) {
            guiGraphics.pose().popMatrix();
            AnimationContext.resume();
            isSuspended = false;
        }

        try {
            guiGraphics.pose().popMatrix();
        } catch (IllegalStateException e) {
            EaseGUIDebug.reportError("pose_stack_underflow", () -> "PoseStack underflow inside AnimationScope close!");
        }

        AnimationContext.popAnimation();
    }

    public boolean isClosed() {
        return isClosed;
    }

    private static float clampScale(float scale) {
        if (Math.abs(scale) < MIN_SCALE) {
            return Math.copySign(MIN_SCALE, scale == 0.0f ? 1.0f : scale);
        }
        return scale;
    }
}