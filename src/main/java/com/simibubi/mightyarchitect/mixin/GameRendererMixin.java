package com.simibubi.mightyarchitect.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.event.LevelRenderLastCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final Minecraft minecraft;

    @Shadow @Final private Camera mainCamera;

    @Unique private final PoseStack poseStack = new PoseStack();

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void mightyarchitect$afterRenderLevel(float partialTicks, long nanoTime, CallbackInfo ci, @Local(ordinal = 1) Matrix4f rotation) {
        this.minecraft.getProfiler().popPush("fabricarchitect_render");

        poseStack.pushPose();
        poseStack.mulPose(rotation);

        LevelRenderLastCallback.EVENT.invoker().onLast(poseStack, partialTicks, mainCamera);

        poseStack.popPose();
    }
}
