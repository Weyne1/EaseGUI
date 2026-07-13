package net.weyne1.easegui.client.animation;

@FunctionalInterface
public interface EasingFunction {
    float apply(float t);
}