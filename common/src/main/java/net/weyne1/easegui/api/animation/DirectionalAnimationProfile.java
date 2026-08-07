package net.weyne1.easegui.api.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectionalAnimationProfile {
    @Nullable private AnimationProfile in;
    @Nullable private AnimationProfile out;

    public DirectionalAnimationProfile(@Nullable AnimationProfile in, @Nullable AnimationProfile out) {
        this.in = in;
        this.out = out;
    }

    @Nullable
    public AnimationProfile getIn() { return in; }

    @Nullable
    public AnimationProfile getOut() { return out; }

    public void setIn(@Nullable AnimationProfile in) { this.in = in; }
    public void setOut(@Nullable AnimationProfile out) { this.out = out; }

    @Nullable
    public AnimationProfile getForDirection(AnimationDirection direction) {
        return direction == AnimationDirection.IN ? in : out;
    }

    // TODO: Использовать после обновления редактора профилей под профили IN/OUT
    @Nullable
    public AnimationProfile getOrFallback(AnimationDirection direction) {
        AnimationProfile exact = getForDirection(direction);
        if (exact != null) return exact;

        if (direction == AnimationDirection.OUT && in != null) {
            return createFallbackOutFromIn(in);
        }

        if (direction == AnimationDirection.IN && out != null) {
            return createFallbackInFromOut(out);
        }

        return null;
    }

    // TODO: Использовать после обновления редактора профилей под профили IN/OUT
    @NotNull
    public AnimationProfile getOrDefault(AnimationDirection direction, @NotNull AnimationProfile defaultProfile) {
        AnimationProfile exact = getForDirection(direction);
        return exact != null ? exact : defaultProfile;
    }

    private static AnimationProfile createFallbackOutFromIn(AnimationProfile in) {
        return in.copy()
                .duration((long) (in.getDuration() * 0.75f))
                .easing(in.getEasing().getInverse());
    }

    private static AnimationProfile createFallbackInFromOut(AnimationProfile out) {
        return out.copy()
                .duration((long) (out.getDuration() * 1.25f))
                .easing(out.getEasing().getInverse());
    }
}