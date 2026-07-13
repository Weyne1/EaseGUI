package net.weyne1.easegui.client.animation;

public enum PivotPoint {
    TOP_LEFT(0.0f, 0.0f),
    TOP_CENTER(0.5f, 0.0f),
    TOP_RIGHT(1.0f, 0.0f),
    CENTER_LEFT(0.0f, 0.5f),
    CENTER(0.5f, 0.5f),
    CENTER_RIGHT(1.0f, 0.5f),
    BOTTOM_LEFT(0.0f, 1.0f),
    BOTTOM_CENTER(0.5f, 1.0f),
    BOTTOM_RIGHT(1.0f, 1.0f);

    private final float xFactor;
    private final float yFactor;

    PivotPoint(float xFactor, float yFactor) {
        this.xFactor = xFactor;
        this.yFactor = yFactor;
    }

    public float getX(float x, float width) {
        return x + (width * this.xFactor);
    }

    public float getY(float y, float height) {
        return y + (height * this.yFactor);
    }
}