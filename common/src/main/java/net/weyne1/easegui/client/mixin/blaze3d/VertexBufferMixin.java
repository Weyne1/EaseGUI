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
    @Unique private float easeGUI$savedR;
    @Unique private float easeGUI$savedG;
    @Unique private float easeGUI$savedB;
    @Unique private float easeGUI$savedA;
    @Unique private boolean easeGUI$hasSavedColor = false;
    @Unique private boolean easeGUI$wasBlendEnabled;

    @Inject(method = "_drawWithShader", at = @At("HEAD"))
    private void easeGUI$injectAlphaBeforeShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, CallbackInfo ci) {
        if (AnimationContext.isActive()) {
            float[] currentColor = RenderSystem.getShaderColor();

            this.easeGUI$savedR = currentColor[0];
            this.easeGUI$savedG = currentColor[1];
            this.easeGUI$savedB = currentColor[2];
            this.easeGUI$savedA = currentColor[3];
            this.easeGUI$hasSavedColor = true;

            this.easeGUI$wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

            float animationAlpha = AnimationContext.getCurrentAlpha();
            float finalAlpha = this.easeGUI$savedA * animationAlpha;

            if (!this.easeGUI$wasBlendEnabled && finalAlpha < 1.0f) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            }

            RenderSystem.setShaderColor(this.easeGUI$savedR, this.easeGUI$savedG, this.easeGUI$savedB, finalAlpha);
        }
    }

    @Inject(method = "_drawWithShader", at = @At("TAIL"))
    private void easeGUI$restoreAlphaAfterShader(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, CallbackInfo ci) {
        if (this.easeGUI$hasSavedColor) {
            RenderSystem.setShaderColor(this.easeGUI$savedR, this.easeGUI$savedG, this.easeGUI$savedB, this.easeGUI$savedA);

            if (!this.easeGUI$wasBlendEnabled) {
                RenderSystem.disableBlend();
            }

            this.easeGUI$hasSavedColor = false;
        }
    }
}