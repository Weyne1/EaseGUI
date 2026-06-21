package net.weyne1.easegui.client.state;

public class RenderContext {
    private static final int MAX_DEPTH = 16; // Максимальная глубина вложенности UI
    private static final float[] alphaStack = new float[MAX_DEPTH];
    private static int stackPointer = 0;

    private static boolean insideListEntry = false;

    public static void enterListEntry() { insideListEntry = true; }
    public static void exitListEntry() { insideListEntry = false; }
    public static boolean isInsideListEntry() { return insideListEntry; }

    public static void startWidgetAnimation(float alpha) {
        if (stackPointer < MAX_DEPTH) {
            alphaStack[stackPointer] = alpha;
            stackPointer++;
        }
    }

    public static void endWidgetAnimation() {
        if (stackPointer > 0) {
            stackPointer--;
        }
    }

    public static boolean isAnimatingWidget() {
        return stackPointer > 0;
    }

    public static float getCurrentAlpha() {
        return stackPointer > 0 ? alphaStack[stackPointer - 1] : 1.0f;
    }
}