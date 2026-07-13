package net.weyne1.easegui.client.animation;

import org.joml.Vector2f;

/**
 * Configuration profile holding animation properties and fluent builder methods.
 */
@SuppressWarnings("UnusedReturnValue")
public class AnimationProfile {

    public boolean enabled = true;
    public long duration = 400;
    public final Vector2f offset = new Vector2f(0f, 0f);
    public final Vector2f startScale = new Vector2f(1f, 1f);
    public float startAlpha = 0.0f;
    public long cascadeDelay = 0L;

    public PivotPoint pivot = PivotPoint.CENTER;
    public EasingType easing = EasingType.EASE_OUT_QUAD;
    public CascadeDirection cascadeDirection = CascadeDirection.TOP_TO_BOTTOM;

    public AnimationProfile enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public AnimationProfile duration(long duration) {
        this.duration = duration;
        return this;
    }

    public AnimationProfile offset(float x, float y) {
        this.offset.set(x, y);
        return this;
    }

    public AnimationProfile offsetX(float x) {
        this.offset.x = x;
        return this;
    }

    public AnimationProfile offsetY(float y) {
        this.offset.y = y;
        return this;
    }

    public AnimationProfile startAlpha(float startAlpha) {
        this.startAlpha = startAlpha;
        return this;
    }

    public AnimationProfile startScale(float x, float y) {
        this.startScale.set(x, y);
        return this;
    }

    public AnimationProfile startScale(float scale) {
        this.startScale.set(scale, scale);
        return this;
    }

    public AnimationProfile startScaleX(float x) {
        this.startScale.x = x;
        return this;
    }

    public AnimationProfile startScaleY(float y) {
        this.startScale.y = y;
        return this;
    }

    public AnimationProfile cascadeDelay(long cascadeDelay) {
        this.cascadeDelay = cascadeDelay;
        return this;
    }

    public AnimationProfile pivot(PivotPoint pivot) {
        this.pivot = pivot;
        return this;
    }

    public AnimationProfile easing(EasingType easing) {
        this.easing = easing;
        return this;
    }

    public AnimationProfile cascadeDirection(CascadeDirection cascadeDirection) {
        this.cascadeDirection = cascadeDirection;
        return this;
    }
}