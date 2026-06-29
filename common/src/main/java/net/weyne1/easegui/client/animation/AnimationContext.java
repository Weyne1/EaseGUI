package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.client.EaseGUIDebug;

import java.util.ArrayDeque;
import java.util.Deque;

public final class AnimationContext {
    private static final Deque<Float> ALPHA_STACK = new ArrayDeque<>();
    private static int suspensionDepth = 0;
    private static int parentAnimationDepth = 0;

    public static void pushAnimation(float alpha) {
        ALPHA_STACK.push(alpha);
    }

    public static void popAnimation() {
        if (!ALPHA_STACK.isEmpty()) {
            ALPHA_STACK.pop();
        }
    }

    public static void suspend() {
        suspensionDepth++;
    }

    public static void resume() {
        if (suspensionDepth > 0) {
            suspensionDepth--;
        }
    }

    public static boolean isAnimating() {
        return suspensionDepth == 0 && !ALPHA_STACK.isEmpty();
    }

    public static float getCurrentAlpha() {
        if (suspensionDepth > 0 || ALPHA_STACK.isEmpty()) {
            return 1.0f;
        }
        return ALPHA_STACK.peek();
    }

    public static void pushParentAnimation() {
        parentAnimationDepth++;
    }

    public static void popParentAnimation() {
        if (parentAnimationDepth <= 0) {
            EaseGUIDebug.reportError("parent_anim_underflow", () -> "Parent animation balance broken! popParentAnimation() called too many times.");
            parentAnimationDepth = 0;
            return;
        }
        parentAnimationDepth--;
    }

    public static boolean hasParentAnimation() {
        return parentAnimationDepth > 0;
    }

    public static void resetFrameState() {
        ALPHA_STACK.clear();
        suspensionDepth = 0;
        parentAnimationDepth = 0;
    }
}