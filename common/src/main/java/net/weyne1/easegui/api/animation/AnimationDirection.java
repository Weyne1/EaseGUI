package net.weyne1.easegui.api.animation;

public enum AnimationDirection {
    IN,
    OUT;

    public boolean isOut() {
        return this == OUT;
    }

    public AnimationDirection opposite() {
        return this == IN ? OUT : IN;
    }
}