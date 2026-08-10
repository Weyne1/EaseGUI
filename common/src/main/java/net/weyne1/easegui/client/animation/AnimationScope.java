package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.EaseGUIDebug;
import org.joml.Matrix3x2fStack;

public class AnimationScope implements AutoCloseable {
    public static final AnimationScope NO_OP = new NoOpAnimationScope();
    private static final float MIN_SCALE = 0.001f;

    private final GuiGraphics graphics;
    private final float alpha;

    private boolean isClosed = false;
    private int suspendDepth = 0;

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
        return suspendDepth > 0;
    }

    protected AnimationScope() {
        this.graphics = null;
        this.alpha = 1.0f;
    }

    AnimationScope(GuiGraphics graphics, float alpha) {
        this.graphics = graphics;
        this.alpha = alpha;

        AnimationContext.pushScope(this);
        this.graphics.pose().pushMatrix();
    }

    void setTransformParams(float offsetX, float offsetY, float scaleX, float scaleY, float pivotX, float pivotY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = clampScale(scaleX);
        this.scaleY = clampScale(scaleY);
        this.pivotX = pivotX;
        this.pivotY = pivotY;

        Matrix3x2fStack poseStack = graphics.pose();

        if (this.scaleX != 1.0f || this.scaleY != 1.0f) {
            poseStack.translate(offsetX + pivotX, offsetY + pivotY);
            poseStack.scale(this.scaleX, this.scaleY);
            poseStack.translate(-pivotX, -pivotY);
        } else {
            poseStack.translate(offsetX, offsetY);
        }
    }

    public void applyPivotScale(float pivotX, float pivotY, float scale) {
        if (this.graphics == null) return;

        this.scaleX = clampScale(scale);
        this.scaleY = clampScale(scale);
        this.pivotX = pivotX;
        this.pivotY = pivotY;

        Matrix3x2fStack poseStack = graphics.pose();

        poseStack.translate(pivotX, pivotY);
        poseStack.scale(this.scaleX, this.scaleY);
        poseStack.translate(-pivotX, -pivotY);
    }

    public void suspend() {
        if (isClosed) return;

        if (suspendDepth == 0) {
            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();

            if (scaleX != 1.0f || scaleY != 1.0f) {
                poseStack.translate(pivotX, pivotY);
                poseStack.scale(1.0f / scaleX, 1.0f / scaleY);
                poseStack.translate(-(offsetX + pivotX), -(offsetY + pivotY));
            } else {
                poseStack.translate(-offsetX, -offsetY);
            }
        }

        suspendDepth++;
    }

    public void resume() {
        if (isClosed) return;
        if (suspendDepth == 0) {
            EaseGUIDebug.reportError("suspend_depth_underflow", () -> "resume() called without matching suspend()!");
            return;
        }

        suspendDepth--;

        if (suspendDepth == 0) {
            graphics.pose().popMatrix();
        }
    }

    @Override
    public void close() {
        if (isClosed) return;
        isClosed = true;

        if (suspendDepth > 0) {
            EaseGUIDebug.reportError("scope_closed_while_suspended",
                    () -> "AnimationScope closed while still suspended (depth=" + suspendDepth + ")! " +
                            "A layer likely leaked a suspend() without a matching resume().");
            graphics.pose().popMatrix();
            suspendDepth = 0;
        }

        try {
            graphics.pose().popMatrix();
        } catch (IllegalStateException e) {
            EaseGUIDebug.reportError("pose_stack_underflow", () -> "PoseStack underflow inside AnimationScope close!");
        }

        AnimationContext.popScope(this);
    }

    private static float clampScale(float scale) {
        return Math.max(MIN_SCALE, Math.abs(scale));
    }

    private static final class NoOpAnimationScope extends AnimationScope {
        @Override void setTransformParams(float oX, float oY, float sX, float sY, float pX, float pY) {}
        @Override public void applyPivotScale(float pX, float pY, float s) {}
        @Override public void suspend() {}
        @Override public void resume() {}
        @Override public void close() {}
        @Override public boolean isClosed() { return true; }
        @Override public boolean isSuspended() { return false; }
        @Override public float getAlpha() { return 1.0f; }
    }
}