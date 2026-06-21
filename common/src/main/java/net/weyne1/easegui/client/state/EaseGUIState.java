package net.weyne1.easegui.client.state;

public class EaseGUIState {

    public static final class AnimationData {
        public long startTime = -1;
        public long delay = 0;
        public float baseAlpha = 1f;
        public boolean init = false;
        public int lastRenderFrame = -1;
    }
}