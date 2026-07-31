package net.weyne1.easegui.client.animation;

import net.weyne1.easegui.client.EaseGUIDebug;

import java.util.ArrayDeque;
import java.util.Deque;

public final class AnimationContext {
    private static final Deque<AnimationScope> SCOPE_STACK = new ArrayDeque<>();
    private static final Deque<AnimationScope> DISABLE_SUSPENDED_STACK = new ArrayDeque<>();
    private static final Deque<Boolean> DISABLE_HAD_SCOPE_STACK = new ArrayDeque<>();
    private static int parentAnimationDepth = 0;
    private static int disableDepth = 0;

    public static DisabledScope disable() {
        return new DisabledScope();
    }

    /**
     * Manual (non-AutoCloseable) variant of {@link #disable()}, for use in Mixin
     * {@code @Inject} pairs (HEAD/RETURN) where wrapping the target method body
     * in a try-with-resources is not possible.
     *
     * <p><b>Caller is responsible for guaranteeing a matching {@link #endManualDisable()}
     * call</b> — unlike {@link #disable()}, there is no compiler-enforced pairing here.
     * Prefer {@code @At("RETURN")} over {@code @At("TAIL")} to catch early-return paths,
     * and rely on {@link #resetFrameState()} as a once-per-frame safety net against leaks
     * from uncaught exceptions between the two calls.
     */
    public static void beginManualDisable() {
        AnimationScope current = getCurrentScope();
        boolean hadScope = current != null;

        DISABLE_HAD_SCOPE_STACK.push(hadScope);
        if (hadScope) {
            DISABLE_SUSPENDED_STACK.push(current);
            current.suspend();
        }
        pushDisabled();
    }

    public static void endManualDisable() {
        popDisabled();

        if (DISABLE_HAD_SCOPE_STACK.isEmpty()) {
            EaseGUIDebug.reportError("disable_stack_underflow", () -> "disableEnd() called without matching disableStart()!");
            return;
        }

        boolean hadScope = DISABLE_HAD_SCOPE_STACK.pop();
        if (hadScope) {
            AnimationScope suspended = DISABLE_SUSPENDED_STACK.pop();
            suspended.resume();
        }
    }

    public static final class DisabledScope implements AutoCloseable {
        private final AnimationScope scope;

        private DisabledScope() {
            this.scope = getCurrentScope();
            if (scope != null) {
                scope.suspend();
            }
            pushDisabled();
        }

        @Override
        public void close() {
            popDisabled();
            if (scope != null) {
                scope.resume();
            }
        }
    }

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
        if (isAnimationDisabled() || SCOPE_STACK.isEmpty()) return false;
        for (AnimationScope scope : SCOPE_STACK) {
            if (!scope.isSuspended()) return true;
        }
        return false;
    }

    public static float getCurrentAlpha() {
        if (isAnimationDisabled() || SCOPE_STACK.isEmpty()) {
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

    public static void pushDisabled() {
        disableDepth++;
    }

    public static void popDisabled() {
        if (disableDepth <= 0) {
            EaseGUIDebug.reportError("disable_depth_underflow", () -> "Attempted to call popDisabled() when depth is already 0!");
            disableDepth = 0;
            return;
        }
        disableDepth--;
    }

    public static boolean isAnimationDisabled() {
        return disableDepth > 0;
    }

    public static void resetFrameState() {
        SCOPE_STACK.clear();
        DISABLE_SUSPENDED_STACK.clear();
        DISABLE_HAD_SCOPE_STACK.clear();
        parentAnimationDepth = 0;
        disableDepth = 0;
    }
}