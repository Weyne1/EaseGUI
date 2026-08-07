package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.api.animation.AnimationDirection;

public class WidgetAnimationState {
    public boolean init = false;
    public long startTime = 0;
    public long delay = 0;
    public int lastRenderFrame = 0;

    public AnimationDirection lastDirection = AnimationDirection.IN;
}