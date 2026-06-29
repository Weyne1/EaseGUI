package net.weyne1.easegui.client.state;

import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;

import java.lang.ref.WeakReference;

public class ScreenStateTracker {
    private static long screenOpenTime = -1;
    private static int currentFrameId = 0;
    private static int resizeGraceFrames = 0;
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    private static WeakReference<Screen> lastScreenRef = new WeakReference<>(null);

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