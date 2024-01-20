package com.simibubi.mightyarchitect.mixin;

import com.simibubi.mightyarchitect.event.KeyInputCallback;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(
            method = "keyPress",
            at = {
                    @At("TAIL"),
                    @At(value = "RETURN", ordinal = 4)
            }
    )
    public void mightyarchitect$onKeyPress(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        KeyInputCallback.EVENT.invoker().onInput(key, scanCode, action, modifiers);
    }
}
