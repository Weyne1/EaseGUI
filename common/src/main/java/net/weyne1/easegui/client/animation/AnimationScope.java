package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.weyne1.easegui.client.EaseGUIDebug;
import org.joml.Matrix3x2fStack;

@SuppressWarnings("unused")
public class AnimationScope implements AutoCloseable {
    public static final AnimationScope NO_OP = new NoOpAnimationScope();
    private static final float MIN_SCALE = 0.001f;

    private GuiGraphicsExtractor graphics;
    private float alpha;
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
    public float getAlpha() { return alpha; }
    public boolean isClosed() { return isClosed; }
    public boolean isSuspended() { return suspendDepth > 0; }

    /**
     * Indicates whether this Scope is a real animation.
     * @return {@code true} for real animations, {@code false} for {@link AnimationScope#NO_OP}.
     */
    public boolean isAnimating() { return true; }

    AnimationScope() {
        this.graphics = null;
        this.alpha = 1.0f;
    }

    void init(GuiGraphicsExtractor graphics, float alpha) {
        this.graphics = graphics;
        this.alpha = alpha;
        this.isClosed = false;
        this.suspendDepth = 0;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.pivotX = 0.0f;
        this.pivotY = 0.0f;

        AnimationContext.pushScope(this);
        this.graphics.pose().pushMatrix();
    }

    void pushTransforms(float offsetX, float offsetY, float scaleX, float scaleY, float pivotX, float pivotY) {
        if (this.graphics == null) return;

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
        } else if (offsetX != 0.0f || offsetY != 0.0f) {
            poseStack.translate(offsetX, offsetY);
        }
    }

    void pushTransforms(float pivotX, float pivotY, float scale) {
        pushTransforms(0.0f, 0.0f, scale, scale, pivotX, pivotY);
    }

    void pushTransforms(float pivotX, float pivotY, float scaleX, float scaleY) {
        pushTransforms(0.0f, 0.0f, scaleX, scaleY, pivotX, pivotY);
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

        try {
            if (suspendDepth > 0) {
                EaseGUIDebug.reportError("scope_closed_while_suspended", () -> String.format("AnimationScope closed while suspended (depth=%d)!", suspendDepth));
                while (suspendDepth > 0) {
                    if (graphics != null) {
                        graphics.pose().popMatrix();
                    }
                    suspendDepth--;
                }
            }

            if (graphics != null) {
                try {
                    graphics.pose().popMatrix();
                } catch (IllegalStateException e) {
                    EaseGUIDebug.reportError("pose_stack_underflow", () -> "PoseStack underflow inside AnimationScope close!");
                }
            }
        } finally {
            AnimationContext.popScope(this);
            this.graphics = null;
            AnimationContext.recycleScope(this);
        }
    }

    private static float clampScale(float scale) {
        return Math.max(MIN_SCALE, Math.abs(scale));
    }

    private static final class NoOpAnimationScope extends AnimationScope {
        @Override public boolean isAnimating() { return false; }
        @Override void pushTransforms(float oX, float oY, float sX, float sY, float pX, float pY) {}
        @Override public void suspend() {}
        @Override public void resume() {}
        @Override public void close() {}
        @Override public boolean isClosed() { return true; }
        @Override public boolean isSuspended() { return false; }
        @Override public float getAlpha() { return 1.0f; }
    }
}