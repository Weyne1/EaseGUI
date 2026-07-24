package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphics;
import net.weyne1.easegui.client.EaseGUIDebug;

public final class AnimationScope implements AutoCloseable {
    private static final float MIN_SCALE = 0.001f;

    private final GuiGraphics guiGraphics;
    private final float alpha;

    private boolean isClosed = false;
    private boolean isSuspended = false;

    private float offsetX, offsetY;
    private float scaleX = 1.0f, scaleY = 1.0f;
    private float pivotX, pivotY;

    public AnimationScope(GuiGraphics guiGraphics, float alpha) {
        this.guiGraphics = guiGraphics;
        this.alpha = alpha;

        this.guiGraphics.flush();
        AnimationContext.pushScope(this);
        this.guiGraphics.pose().pushPose();
    }

    void setTransformParams(float offsetX, float offsetY, float scaleX, float scaleY, float pivotX, float pivotY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scaleX = clampScale(scaleX);
        this.scaleY = clampScale(scaleY);
        this.pivotX = pivotX;
        this.pivotY = pivotY;

        if (this.scaleX != 1.0f || this.scaleY != 1.0f) {
            guiGraphics.pose().translate(offsetX + pivotX, offsetY + pivotY, 0.0f);
            guiGraphics.pose().scale(this.scaleX, this.scaleY, 1.0f);
            guiGraphics.pose().translate(-pivotX, -pivotY, 0.0f);
        } else {
            guiGraphics.pose().translate(offsetX, offsetY, 0.0f);
        }
    }

    public void suspend() {
        if (isClosed || isSuspended) return;

        this.guiGraphics.flush();
        guiGraphics.pose().pushPose();

        if (scaleX != 1.0f || scaleY != 1.0f) {
            guiGraphics.pose().translate(pivotX, pivotY, 0.0f);
            guiGraphics.pose().scale(1.0f / scaleX, 1.0f / scaleY, 1.0f);
            guiGraphics.pose().translate(-(offsetX + pivotX), -(offsetY + pivotY), 0.0f);
        } else {
            guiGraphics.pose().translate(-offsetX, -offsetY, 0.0f);
        }

        isSuspended = true;
    }

    public void resume() {
        if (isClosed || !isSuspended) return;

        this.guiGraphics.flush();
        guiGraphics.pose().popPose();

        isSuspended = false;
    }

    @Override
    public void close() {
        if (isClosed) return;
        isClosed = true;

        this.guiGraphics.flush();

        if (isSuspended) {
            guiGraphics.pose().popPose();
            isSuspended = false;
        }

        try {
            guiGraphics.pose().popPose();
        } catch (IllegalStateException e) {
            EaseGUIDebug.reportError("pose_stack_underflow", () -> "PoseStack underflow inside AnimationScope close!");
        }

        AnimationContext.popScope(this);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isSuspended() {
        return isSuspended;
    }

    public float getAlpha() {
        return alpha;
    }

    private static float clampScale(float scale) {
        return Math.max(MIN_SCALE, Math.abs(scale));
    }
}