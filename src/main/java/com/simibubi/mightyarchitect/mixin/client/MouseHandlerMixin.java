package com.simibubi.mightyarchitect.mixin.client;

import com.simibubi.mightyarchitect.event.MouseButtonPressedCallback;
import com.simibubi.mightyarchitect.event.MouseScrollCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "onPress",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;onMouseMiddleClick()V",
                            shift = At.Shift.AFTER
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
                            shift = At.Shift.AFTER
                    )
            }
    )
    public void onMouseButtonPressed(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        MouseButtonPressedCallback.EVENT.invoker().onInput(button, modifiers);
    }

    @Inject(
            method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
            )
    )
    public void onScrollDelta(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        var scrollDelta = (this.minecraft.options.discreteMouseScroll ? Math.signum(yOffset) : yOffset) * this.minecraft.options.mouseWheelSensitivity;
        MouseScrollCallback.EVENT.invoker().onScroll((MouseHandler) (Object) this, scrollDelta);
    }
}
