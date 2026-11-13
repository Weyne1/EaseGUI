package net.weyne1.easegui.client.mixin.blaze3d;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {

    @WrapMethod(method = "_drawWithShader")
    private void easegui$wrapDrawWithShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, Operation<Void> original) {
        if (!AnimationContext.isActive()) {
            original.call(modelViewMatrix, projectionMatrix, shader);
            return;
        }

        float[] currentColor = RenderSystem.getShaderColor();
        float r = currentColor[0];
        float g = currentColor[1];
        float b = currentColor[2];
        float a = currentColor[3];
        boolean wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

        float animatedAlpha = a * AnimationContext.getCurrentAlpha();

        if (!wasBlendEnabled && animatedAlpha < 1.0f) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        RenderSystem.setShaderColor(r, g, b, animatedAlpha);

        try {
            original.call(modelViewMatrix, projectionMatrix, shader);
        } finally {
            RenderSystem.setShaderColor(r, g, b, a);
            if (!wasBlendEnabled) {
                RenderSystem.disableBlend();
            }
        }
    }
}