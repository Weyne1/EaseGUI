package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.EaseGUIDebug;
import org.joml.Matrix3x2fStack;

public final class AnimationScope implements AutoCloseable {
    private static final float MIN_SCALE = 0.001f;

    private final GuiGraphics guiGraphics;
    private final float alpha;

    private boolean isClosed = false;
    private boolean isSuspended = false;

    private float offsetX, offsetY;
    private float scaleX = 1.0f, scaleY = 1.0f;
    private float pivotX, pivotY;

    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }

    public float getScaleX() { return scaleX; }
    public float getScaleY() { return scaleY; }

    public float getPivotX() { return pivotX; }
    public float getPivotY() { return pivotY; }

    public float getAlpha() {
        return alpha;
    }

    public boolean isClosed() {
        return isClosed;
    }
    public boolean isSuspended() {
        return isSuspended;
    }

    public AnimationScope(GuiGraphics guiGraphics, float alpha) {
        this.guiGraphics = guiGraphics;
        this.alpha = alpha;

        AnimationContext.pushScope(this);
        this.guiGraphics.pose().pushMatrix();
    }

    void setTransformParams(float offsetX, float offsetY, float scaleX, float scaleY, float pivotX, float pivotY) {
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

        isSuspended = false;
    }

    @Override
    public void close() {
        if (isClosed) return;
        isClosed = true;

        if (isSuspended) {
            guiGraphics.pose().popMatrix();
            isSuspended = false;
        }

        try {
            guiGraphics.pose().popMatrix();
        } catch (IllegalStateException e) {
            EaseGUIDebug.reportError("pose_stack_underflow", () -> "PoseStack underflow inside AnimationScope close!");
        }

        AnimationContext.popScope(this);
    }

    private static float clampScale(float scale) {
        return Math.max(MIN_SCALE, Math.abs(scale));
    }
}