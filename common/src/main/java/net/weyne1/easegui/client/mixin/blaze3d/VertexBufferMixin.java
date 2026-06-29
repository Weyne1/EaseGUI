package net.weyne1.easegui.client.mixin.blaze3d;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.ShaderInstance;
import net.weyne1.easegui.client.animation.AnimationContext;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin {
    @Unique private float[] easeGUI$savedShaderColor;
    @Unique private boolean easeGUI$wasBlendEnabled;

    @Inject(method = "_drawWithShader", at = @At("HEAD"))
    private void easeGUI$injectAlphaBeforeShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, CallbackInfo ci) {
        if (AnimationContext.isAnimating()) {
            float[] currentColor = RenderSystem.getShaderColor();
            this.easeGUI$savedShaderColor = currentColor.clone();
            this.easeGUI$wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

            String shaderName = shader.getName();
            float animationAlpha = AnimationContext.getCurrentAlpha();

            if (shaderName.toLowerCase().contains("glint")) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, currentColor[3] * animationAlpha);
            } else {
                float finalAlpha = currentColor[3] * animationAlpha;

                if (!this.easeGUI$wasBlendEnabled && finalAlpha < 1.0f) {
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                }
                RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], finalAlpha);
            }
        }
    }

    @Inject(method = "_drawWithShader", at = @At("TAIL"))
    private void easeGUI$restoreAlphaAfterShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, CallbackInfo ci) {
        if (this.easeGUI$savedShaderColor != null) {
            RenderSystem.setShaderColor(
                    this.easeGUI$savedShaderColor[0],
                    this.easeGUI$savedShaderColor[1],
                    this.easeGUI$savedShaderColor[2],
                    this.easeGUI$savedShaderColor[3]
            );

            if (!this.easeGUI$wasBlendEnabled) {
                RenderSystem.disableBlend();
            }
            this.easeGUI$savedShaderColor = null;
        }
    }
}