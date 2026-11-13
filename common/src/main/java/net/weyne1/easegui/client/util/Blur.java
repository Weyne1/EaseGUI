package net.weyne1.easegui.client.util;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.weyne1.easegui.client.animation.AnimationContext;
import net.weyne1.easegui.client.mixin.accessor.PostChainAccessor;
import org.lwjgl.opengl.GL11;

public class Blur {
    private static final float MAX_BLUR_RADIUS = 8f;
    private static PostChain blurEffect;
    private static int lastWidth = 0;
    private static int lastHeight = 0;

    public static void renderBlur(float progress, float partialTick) {
        if (AnimationContext.isAnimationDisabled()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        boolean sizeChanged = mc.getWindow().getWidth() != lastWidth || mc.getWindow().getHeight() != lastHeight;
        if (blurEffect == null || sizeChanged) {
            initBlur(mc);
        }
        if (blurEffect == null) return;

        for (PostPass pass : ((PostChainAccessor) blurEffect).getPasses()) {
            Uniform radius = pass.getEffect().getUniform("Radius");
            if (radius != null) radius.set(MAX_BLUR_RADIUS);

            Uniform progressUniform = pass.getEffect().getUniform("Progress");
            if (progressUniform != null) progressUniform.set(progress);
        }

        blurEffect.process(partialTick);

        mc.getMainRenderTarget().bindWrite(true);
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
    }

    private static void initBlur(Minecraft mc) {
        try {
            if (blurEffect != null) blurEffect.close();

            lastWidth = mc.getWindow().getWidth();
            lastHeight = mc.getWindow().getHeight();

            blurEffect = new PostChain(
                    mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(),
                    new ResourceLocation("easegui", "shaders/post/blur.json"));
            blurEffect.resize(lastWidth, lastHeight);
        } catch (Exception e) {
            blurEffect = null;
        }
    }
}