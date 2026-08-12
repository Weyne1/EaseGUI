package net.weyne1.easegui.client.animation;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.weyne1.easegui.client.EaseGUIDebug;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("unused")
public final class AnimationContext {
    private static final class ThreadState {
        final Deque<AnimationScope> scopeStack = new ArrayDeque<>();
        final Deque<AnimationScope> disableSuspendedStack = new ArrayDeque<>();
        final Deque<Boolean> disableHadScopeStack = new ArrayDeque<>();
        final Deque<AnimationScope> scopePool = new ArrayDeque<>();
        int parentAnimationDepth = 0;
        int disableDepth = 0;

        void reset() {
            scopeStack.clear();
            disableSuspendedStack.clear();
            disableHadScopeStack.clear();
            parentAnimationDepth = 0;
            disableDepth = 0;
        }
    }

    private static final ThreadLocal<ThreadState> STATE = ThreadLocal.withInitial(ThreadState::new);

    @NotNull
    static AnimationScope obtainScope(GuiGraphicsExtractor graphics, float alpha) {
        ThreadState state = STATE.get();
        AnimationScope scope = state.scopePool.isEmpty() ? new AnimationScope() : state.scopePool.pop();
        scope.init(graphics, alpha);
        return scope;
    }

    static void recycleScope(AnimationScope scope) {
        if (scope == AnimationScope.NO_OP) return;

        ThreadState state = STATE.get();

        if (state.scopePool.size() < 64) {
            state.scopePool.push(scope);
        }
    }

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
        ThreadState state = STATE.get();
        AnimationScope current = getCurrentScope();
        boolean hadScope = current.isAnimating();

        state.disableHadScopeStack.push(hadScope);
        if (hadScope) {
            state.disableSuspendedStack.push(current);
            current.suspend();
        }
        pushDisabled();
    }

    public static void endManualDisable() {
        popDisabled();

        ThreadState state = STATE.get();
        if (state.disableHadScopeStack.isEmpty()) {
            EaseGUIDebug.reportError("disable_stack_underflow", () -> "endManualDisable() called without matching beginManualDisable()!");
            return;
        }

        boolean hadScope = state.disableHadScopeStack.pop();
        if (hadScope) {
            if (state.disableSuspendedStack.isEmpty()) {
                EaseGUIDebug.reportError("disable_suspended_underflow", () -> "Suspended stack underflow in endManualDisable()!");
                return;
            }
            AnimationScope suspended = state.disableSuspendedStack.pop();
            suspended.resume();
        }
    }

    public static final class DisabledScope implements AutoCloseable {
        private final AnimationScope scope;

        private DisabledScope() {
            this.scope = getCurrentScope();
            if (scope.isAnimating()) {
                scope.suspend();
            }
            pushDisabled();
        }

        @Override
        public void close() {
            popDisabled();
            if (scope.isAnimating()) {
                scope.resume();
            }
        }
    }

    public static void pushScope(AnimationScope scope) {
        STATE.get().scopeStack.push(scope);
    }

    public static void popScope(AnimationScope scope) {
        ThreadState state = STATE.get();
        if (state.scopeStack.isEmpty()) {
            EaseGUIDebug.reportError("scope_underflow", () -> "Attempt to close a scope when the stack is empty!");
            return;
        }

        if (state.scopeStack.peek() == scope) {
            state.scopeStack.pop();
        } else {
            EaseGUIDebug.reportError("scope_mismatch", () -> "Animation closing order violated! Attempted to close the wrong scope.");
        }
    }

    @NotNull
    public static AnimationScope getCurrentScope() {
        ThreadState state = STATE.get();
        if (state.scopeStack.isEmpty()) {
            return AnimationScope.NO_OP;
        }
        return state.scopeStack.peek();
    }

    public static boolean isActive() {
        ThreadState state = STATE.get();
        if (isAnimationDisabled() || state.scopeStack.isEmpty()) return false;
        for (AnimationScope scope : state.scopeStack) {
            if (!scope.isSuspended()) return true;
        }
        return false;
    }

    public static float getCurrentAlpha() {
        ThreadState state = STATE.get();
        if (isAnimationDisabled() || state.scopeStack.isEmpty()) {
            return 1.0f;
        }

        float totalAlpha = 1.0f;
        boolean hasActiveScope = false;

        for (AnimationScope scope : state.scopeStack) {
            if (!scope.isSuspended()) {
                totalAlpha *= scope.getAlpha();
                hasActiveScope = true;
            }
        }

        return hasActiveScope ? totalAlpha : 1.0f;
    }

    public static void pushParentAnimation() {
        STATE.get().parentAnimationDepth++;
    }

    public static void popParentAnimation() {
        ThreadState state = STATE.get();
        if (state.parentAnimationDepth <= 0) {
            EaseGUIDebug.reportError("parent_anim_underflow", () -> "Parent animation balance broken! popParentAnimation() called too many times.");
            state.parentAnimationDepth = 0;
            return;
        }
        state.parentAnimationDepth--;
    }

    public static boolean hasParentAnimation() {
        return STATE.get().parentAnimationDepth > 0;
    }

    public static void pushDisabled() {
        STATE.get().disableDepth++;
    }

    public static void popDisabled() {
        ThreadState state = STATE.get();
        if (state.disableDepth <= 0) {
            EaseGUIDebug.reportError("disable_depth_underflow", () -> "Attempted to call popDisabled() when depth is already 0!");
            state.disableDepth = 0;
            return;
        }
        state.disableDepth--;
    }

    public static boolean isAnimationDisabled() {
        return STATE.get().disableDepth > 0;
    }

    public static void resetFrameState() {
        STATE.get().reset();
    }
}