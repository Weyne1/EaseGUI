package net.weyne1.easegui.api.animation;

import org.joml.Vector2f;

/**
 * Configuration profile holding animation properties and fluent builder methods.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class AnimationProfile {
    public static final AnimationProfile EMPTY = new AnimationProfile().enabled(false);
    private boolean enabled = true;
    private long duration = 400;
    private final Vector2f initialOffset = new Vector2f();
    private final Vector2f initialScale = new Vector2f(1f, 1f);
    private float initialAlpha = 0.0f;
    private long cascadeDelay = 0L;
    private PivotPoint pivot = PivotPoint.CENTER;
    private EasingType easing = EasingType.EASE_OUT_QUAD;
    private CascadeDirection cascadeDirection = CascadeDirection.TOP_TO_BOTTOM;

    public boolean isEnabled() { return enabled;}
    public long getDuration() { return duration; }
    public float getInitialAlpha() { return initialAlpha; }
    public float getInitialScaleX() { return initialScale.x; }
    public float getInitialScaleY() { return initialScale.y; }
    public float getInitialOffsetX() { return initialOffset.x; }
    public float getInitialOffsetY() { return initialOffset.y; }
    public long getCascadeDelay() { return cascadeDelay; }
    public PivotPoint getPivot() { return pivot; }
    public EasingType getEasing() { return easing; }
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

    public AnimationProfile initialOffset(float x, float y) {
        this.initialOffset.set(x, y);
        return this;
    }

    public AnimationProfile initialOffsetX(float x) {
        this.initialOffset.x = x;
        return this;
    }

    public AnimationProfile initialOffsetY(float y) {
        this.initialOffset.y = y;
        return this;
    }

    public AnimationProfile initialAlpha(float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("Alpha must be between 0.0 and 1.0.");
        }

        this.initialAlpha = alpha;
        return this;
    }

    public AnimationProfile initialScale(float x, float y) {
        this.initialScale.set(x, y);
        return this;
    }

    public AnimationProfile initialScale(float scale) {
        return initialScale(scale, scale);
    }

    public AnimationProfile initialScaleX(float x) {
        this.initialScale.x = x;
        return this;
    }

    public AnimationProfile initialScaleY(float y) {
        this.initialScale.y = y;
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
        copy.initialOffset.set(this.initialOffset);
        copy.initialScale.set(this.initialScale);
        copy.initialAlpha = this.initialAlpha;
        copy.cascadeDelay = this.cascadeDelay;
        copy.pivot = this.pivot;
        copy.easing = this.easing;
        copy.cascadeDirection = this.cascadeDirection;

        return copy;
    }
}