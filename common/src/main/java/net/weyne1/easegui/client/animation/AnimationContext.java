package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.client.EaseGUIDebug;

import java.util.ArrayDeque;
import java.util.Deque;

public final class AnimationContext {
    private static final Deque<AnimationScope> SCOPE_STACK = new ArrayDeque<>();
    private static int parentAnimationDepth = 0;

    public static void pushScope(AnimationScope scope) {
        SCOPE_STACK.push(scope);
    }

    public static void popScope(AnimationScope scope) {
        if (SCOPE_STACK.isEmpty()) {
            EaseGUIDebug.reportError("scope_underflow", () -> "Attempt to close a scope when the stack is empty!");
            return;
        }

        if (SCOPE_STACK.peek() == scope) {
            SCOPE_STACK.pop();
        } else {
            EaseGUIDebug.reportError("scope_mismatch", () -> "Animation closing order violated! Attempted to close the wrong scope.");
        }
    }

    public static AnimationScope getCurrentScope() {
        return SCOPE_STACK.peek();
    }

    public static boolean isActive() {
        if (SCOPE_STACK.isEmpty()) return false;
        for (AnimationScope scope : SCOPE_STACK) {
            if (!scope.isSuspended()) return true;
        }
        return false;
    }

    public static float getCurrentAlpha() {
        if (SCOPE_STACK.isEmpty()) {
            return 1.0f;
        }

        float totalAlpha = 1.0f;
        boolean hasActiveScope = false;

        for (AnimationScope scope : SCOPE_STACK) {
            if (!scope.isSuspended()) {
                totalAlpha *= scope.getAlpha();
                hasActiveScope = true;
            }
        }

        return hasActiveScope ? totalAlpha : 1.0f;
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
        SCOPE_STACK.clear();
        parentAnimationDepth = 0;
    }
}