package com.simibubi.mightyarchitect.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.event.LevelRenderLastCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final Minecraft minecraft;

    @Shadow @Final private Camera mainCamera;

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void mightyarchitect$afterRenderLevel(float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci) {
        this.minecraft.getProfiler().popPush("fabricarchitect_render");
        LevelRenderLastCallback.EVENT.invoker().onLast(poseStack, partialTicks, mainCamera);
    }
}
