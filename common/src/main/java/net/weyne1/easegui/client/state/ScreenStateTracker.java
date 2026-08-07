package net.weyne1.easegui.client.state;

import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
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

    private static WeakReference<Screen> lastScreenRef = new WeakReference<>(null);

    private static boolean closing = false;
    private static long closingStartTime = -1L;
    private static long maxOutDuration = 0L;
    private static Screen pendingScreen = null;
    private static boolean bypassInterceptor = false;

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
        closing = false;
        pendingScreen = null;
    }

    public static boolean isClosing() {
        return closing;
    }

    public static long getClosingStartTime() {
        return closingStartTime;
    }

    public static boolean isBypassInterceptor() {
        return bypassInterceptor;
    }

    public static void setPendingScreen(Screen screen) {
        pendingScreen = screen;
    }

    public static boolean startClosingProcedure(Screen nextScreen, long maxOutTime) {
        if (bypassInterceptor) return false;

        if (maxOutTime <= 0) {
            return false;
        }

        closing = true;
        pendingScreen = nextScreen;
        closingStartTime = Util.getMillis();
        maxOutDuration = maxOutTime;

        return true;
    }

    public static void checkClosingProgress() {
        if (!closing) return;

        long elapsed = Util.getMillis() - closingStartTime;
        if (elapsed >= maxOutDuration) {
            finishClosing();
        }
    }

    private static void finishClosing() {
        closing = false;
        bypassInterceptor = true;
        Minecraft.getInstance().gui.setScreen(pendingScreen);
        bypassInterceptor = false;
        pendingScreen = null;
    }

    public static boolean isResizeFrame() {
        return resizeGraceFrames > 0;
    }

    public static void incrementFrame() {
        currentFrameId++;

        var minecraft = Minecraft.getInstance();
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