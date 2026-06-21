package net.weyne1.easegui.client.state;

import net.minecraft.Util;

public class ScreenStateTracker {
    private static long screenOpenTime = -1;
    private static int currentFrameId = 0;

    /**
     * Called via mixin when Minecraft.setScreen() occurs.
     */
    public static void markScreenOpened() {
        screenOpenTime = -1;
        currentFrameId = 0;
    }

    /**
     * Returns the timestamp when the screen was actually first rendered.
     */
    public static long getScreenOpenTime() {
        if (screenOpenTime == -1) {
            screenOpenTime = Util.getMillis();
        }
        return screenOpenTime;
    }

    public static void incrementFrame() {
        currentFrameId++;
    }

    public static int getCurrentFrameId() {
        return currentFrameId;
    }
}