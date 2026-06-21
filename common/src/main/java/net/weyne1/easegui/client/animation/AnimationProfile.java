package net.weyne1.easegui.client.animation;

public class AnimationProfile {
    public boolean enabled = true;
    public long duration = 400;
    public float offsetX = 0f;
    public float offsetY = 0f;
    public float startAlpha = 0.0f;
    public float startScaleX = 1.0f;
    public float startScaleY = 1.0f;
    public long cascadeDelay = 0L;
    public PivotPoint pivot = PivotPoint.CENTER;
    public EasingType easing = EasingType.EASE_OUT_QUAD;
    public CascadeDirection cascadeDirection = CascadeDirection.TOP_TO_BOTTOM;

    public AnimationProfile enabled(boolean enabled) { this.enabled = enabled; return this; }
    public AnimationProfile duration(long duration) { this.duration = duration; return this; }
    public AnimationProfile offsetX(float offsetX) { this.offsetX = offsetX; return this; }
    public AnimationProfile offsetY(float offsetY) { this.offsetY = offsetY; return this; }
    public AnimationProfile startAlpha(float startAlpha) { this.startAlpha = startAlpha; return this; }
    public AnimationProfile startScaleX(float startScale) { this.startScaleX = startScale; return this; }
    public AnimationProfile startScaleY(float startScale) { this.startScaleY = startScale; return this; }
    public AnimationProfile cascadeDelay(long cascadeDelay) { this.cascadeDelay = cascadeDelay; return this; }
    public AnimationProfile pivot(PivotPoint pivot) { this.pivot = pivot; return this; }
    public AnimationProfile easing(EasingType easing) { this.easing = easing; return this; }
    public AnimationProfile cascadeDirection(CascadeDirection cascadeDirection) { this.cascadeDirection = cascadeDirection; return this; }

    @FunctionalInterface
    public interface EasingFunction {
        float apply(float t);
    }

    public enum PivotPoint {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        CENTER_LEFT, CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public enum EasingType {
        LINEAR(t -> t),

        EASE_IN_QUAD(t -> t * t),
        EASE_OUT_QUAD(t -> 1f - (1f - t) * (1f - t)),
        EASE_IN_OUT_QUAD(t -> t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f),

        EASE_IN_CUBIC(t -> t * t * t),
        EASE_OUT_CUBIC(t -> 1f - (float) Math.pow(1f - t, 3)),
        EASE_IN_OUT_CUBIC(t -> t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f),

        EASE_OUT_QUINT(t -> 1f - (float) Math.pow(1f - t, 5)),

        EASE_OUT_EXPO(t -> t == 1f ? 1f : 1f - (float) Math.pow(2, -10f * t)),

        EASE_IN_BACK(t -> {
            float c1 = 1.70158f;
            float c3 = c1 + 1f;
            return c3 * t * t * t - c1 * t * t;
        }),
        EASE_OUT_BACK(t -> {
            float c1 = 1.70158f;
            float c3 = c1 + 1f;
            return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
        }),

        EASE_OUT_ELASTIC(t -> {
            if (t == 0f) return 0f;
            if (t == 1f) return 1f;
            float c4 = (2f * (float) Math.PI) / 3f;
            return (float) Math.pow(2, -10f * t) * (float) Math.sin((t * 10f - 0.75f) * c4) + 1f;
        }),

        EASE_OUT_BOUNCE(t -> {
            float n1 = 7.5625f;
            float d1 = 2.75f;

            if (t < 1f / d1) {
                return n1 * t * t;
            } else if (t < 2f / d1) {
                float t2 = t - 1.5f / d1;
                return n1 * t2 * t2 + 0.75f;
            } else if (t < 2.5f / d1) {
                float t2 = t - 2.25f / d1;
                return n1 * t2 * t2 + 0.9375f;
            } else {
                float t2 = t - 2.625f / d1;
                return n1 * t2 * t2 + 0.984375f;
            }
        });

        private final EasingFunction function;

        EasingType(EasingFunction function) {
            this.function = function;
        }

        public float ease(float t) {
            return function.apply(t);
        }
    }

    public enum CascadeDirection {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }
}