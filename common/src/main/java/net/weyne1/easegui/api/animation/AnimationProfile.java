package net.weyne1.easegui.api.animation;

import org.joml.Vector2f;

/**
 * Configuration profile holding animation properties and fluent builder methods.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class AnimationProfile {

    private boolean enabled = true;
    private long duration = 400;
    private final Vector2f offset = new Vector2f();
    private final Vector2f startScale = new Vector2f(1f, 1f);
    private float startAlpha = 0.0f;
    private long cascadeDelay = 0L;

    private PivotPoint pivot = PivotPoint.CENTER;
    private EasingType easing = EasingType.EASE_OUT_QUAD;
    private CascadeDirection cascadeDirection = CascadeDirection.TOP_TO_BOTTOM;

    public boolean isEnabled() {
        return enabled;
    }

    public long getDuration() {
        return duration;
    }

    public float getOffsetX() {
        return offset.x;
    }

    public float getOffsetY() {
        return offset.y;
    }

    public float getStartScaleX() {
        return startScale.x;
    }

    public float getStartScaleY() {
        return startScale.y;
    }

    public float getStartAlpha() {
        return startAlpha;
    }

    public long getCascadeDelay() {
        return cascadeDelay;
    }

    public PivotPoint getPivot() {
        return pivot;
    }

    public EasingType getEasing() {
        return easing;
    }

    public CascadeDirection getCascadeDirection() {
        return cascadeDirection;
    }

    public AnimationProfile enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public AnimationProfile duration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration must be non-negative.");
        }

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
        if (startAlpha < 0.0f || startAlpha > 1.0f) {
            throw new IllegalArgumentException("Start alpha must be between 0.0 and 1.0.");
        }

        this.startAlpha = startAlpha;
        return this;
    }

    public AnimationProfile startScale(float x, float y) {
        this.startScale.set(x, y);
        return this;
    }

    public AnimationProfile startScale(float scale) {
        return startScale(scale, scale);
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
        if (cascadeDelay < 0) {
            throw new IllegalArgumentException("Cascade delay must be non-negative.");
        }

        this.cascadeDelay = cascadeDelay;
        return this;
    }

    public AnimationProfile pivot(PivotPoint pivot) {
        if (pivot == null) {
            throw new NullPointerException("Pivot point cannot be null.");
        }

        this.pivot = pivot;
        return this;
    }

    public AnimationProfile easing(EasingType easing) {
        if (easing == null) {
            throw new NullPointerException("Easing type cannot be null.");
        }

        this.easing = easing;
        return this;
    }

    public AnimationProfile cascadeDirection(CascadeDirection cascadeDirection) {
        if (cascadeDirection == null) {
            throw new NullPointerException("Cascade direction cannot be null.");
        }

        this.cascadeDirection = cascadeDirection;
        return this;
    }

    /**
     * Creates a deep copy of this animation profile.
     */
    public AnimationProfile copy() {
        AnimationProfile copy = new AnimationProfile();

        copy.enabled = this.enabled;
        copy.duration = this.duration;
        copy.offset.set(this.offset);
        copy.startScale.set(this.startScale);
        copy.startAlpha = this.startAlpha;
        copy.cascadeDelay = this.cascadeDelay;
        copy.pivot = this.pivot;
        copy.easing = this.easing;
        copy.cascadeDirection = this.cascadeDirection;

        return copy;
    }
}