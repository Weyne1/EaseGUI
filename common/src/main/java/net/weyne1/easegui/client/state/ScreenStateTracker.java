package net.weyne1.easegui.client.state;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;

import java.lang.ref.WeakReference;

public class ScreenStateTracker {
    private static long screenOpenTime = -1;
    private static long lastTrackedSessionTime = -1L;
    private static long titleActualStartTime = -1L;
    private static int currentFrameId = 0;
    private static int resizeGraceFrames = 0;
    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static boolean blurredInCurrentFrame = false;
    private static boolean lastScreenWasBlurred = false;

    private static WeakReference<Screen> lastScreenRef = new WeakReference<>(null);
    private static WeakReference<Screen> lastRenderedScreenRef = new WeakReference<>(null);

    public static boolean checkAndTrackNewScreen(Screen screen) {
        Screen lastScreen = lastScreenRef.get();
        if (lastScreen == screen) {
            return false;
        }
        lastScreenRef = new WeakReference<>(screen);
        return true;
    }

    public static void markScreenOpened() {
        screenOpenTime = -1;
        resizeGraceFrames = 0;
    }

    public static void markScreenRendered(Screen screen) {
        lastRenderedScreenRef = new WeakReference<>(screen);
    }

    public static boolean wasScreenRendered(Screen screen) {
        return screen != null && lastRenderedScreenRef.get() == screen;
    }

    public static void markBlurredThisFrame() {
        blurredInCurrentFrame = true;
    }

    public static void onScreenChange() {
        lastScreenWasBlurred = blurredInCurrentFrame;
        blurredInCurrentFrame = false;
    }

    public static boolean wasLastScreenBlurred() {
        return lastScreenWasBlurred;
    }

    public static boolean isResizeFrame() {
        return resizeGraceFrames > 0;
    }

    public static void incrementFrame() {
        currentFrameId++;

        var minecraft = net.minecraft.client.Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();

        if (lastWidth != -1 && (width != lastWidth || height != lastHeight)) {
            resizeGraceFrames = 3;
        }

        lastWidth = width;
        lastHeight = height;

        if (resizeGraceFrames > 0) {
            resizeGraceFrames--;
        }
    }

    public static long getTitleActualStartTime() {
        long openTime = getScreenOpenTime();

        if (lastTrackedSessionTime != openTime) {
            lastTrackedSessionTime = openTime;
            titleActualStartTime = Util.getMillis();
        }

        return titleActualStartTime;
    }

    public static long getScreenOpenTime() {
        if (screenOpenTime == -1) {
            screenOpenTime = Util.getMillis();
        }
        return screenOpenTime;
    }

    public static long getScreenElapsed() {
        return Util.getMillis() - getScreenOpenTime();
    }

    public static int getCurrentFrameId() {
        return currentFrameId;
    }
}