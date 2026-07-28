package net.weyne1.easegui.client.animation;

/**
 * Utility for applying UI animation transformations (scale, offset)
 * to Minecraft's Picture-in-Picture (PiP) render states.
 * <p>
 * Used to smoothly animate 3D models (skins, books, banners, etc.)
 * relative to the current {@link AnimationScope} pivot.
 */
public final class PipTransform {
    private PipTransform() {}

    private static final int MIN_SIZE = 1;

    public static int[] transformRangeX(int x0, int x1) {
        return transformRange(x0, x1, true);
    }

    public static int[] transformRangeY(int y0, int y1) {
        return transformRange(y0, y1, false);
    }

    private static int[] transformRange(int start, int end, boolean horizontal) {
        AnimationScope scope = AnimationContext.getCurrentScope();
        if (scope == null || scope.isSuspended()) {
            return new int[]{start, end};
        }

        float pivot = horizontal ? scope.getPivotX() : scope.getPivotY();
        float offset = horizontal ? scope.getOffsetX() : scope.getOffsetY();
        float scale = horizontal ? scope.getScaleX() : scope.getScaleY();

        float newStart = pivot + offset + (start - pivot) * scale;
        float newEnd = pivot + offset + (end - pivot) * scale;

        int roundedStart = Math.round(newStart);
        int roundedEnd = Math.round(newEnd);

        int min = Math.min(roundedStart, roundedEnd);
        int max = Math.max(roundedStart, roundedEnd);

        if (max - min < MIN_SIZE) {
            max = min + MIN_SIZE;
        }

        return new int[]{min, max};
    }

    public static float scale(float scale) {
        AnimationScope scope = AnimationContext.getCurrentScope();
        if (scope == null || scope.isSuspended()) return scale;

        float animScale = (Math.abs(scope.getScaleX()) + Math.abs(scope.getScaleY())) * 0.5f;
        return scale * animScale;
    }
}