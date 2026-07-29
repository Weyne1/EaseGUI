package net.weyne1.easegui.client.compat;

import java.lang.reflect.Field;

public class WatutCompat {
    private static boolean checked = false;
    private static Field performingOwnRenderField = null;
    private static Field particleGuiField = null;

    public static boolean isWatutRendering() {
        if (!checked) {
            try {
                Class<?> renderHelper = Class.forName("com.corosus.watut.client.screen.RenderHelper");
                performingOwnRenderField = renderHelper.getField("performingOwnRender");

                Class<?> particleRenderer = Class.forName("com.corosus.watut.client.screen.ScreenParticleRenderer");
                particleGuiField = particleRenderer.getField("isRenderingParticleGUI");
            } catch (Exception ignored) { }
            checked = true;
        }

        try {
            if (performingOwnRenderField != null && performingOwnRenderField.getBoolean(null)) {
                return true;
            }
            if (particleGuiField != null && particleGuiField.getBoolean(null)) {
                return true;
            }
        } catch (Exception ignored) {}

        return false;
    }
}