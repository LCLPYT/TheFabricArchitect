package com.simibubi.mightyarchitect.mixin;

import com.simibubi.mightyarchitect.event.ChatReceivedClientCallback;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.time.Instant;
import java.util.Objects;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void narrateChatMessage(ChatType.Bound boundChatType, Component message);

    @Shadow protected abstract void logSystemMessage(Component message, Instant timestamp);

    @Shadow private long previousMessageTime;

    @Inject(
            method = "method_45745",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    public void mightyarchitect$lambda$handleDisguisedChatMessage(ChatType.Bound bound, Component component,
                                                                  Instant instant, CallbackInfoReturnable<Boolean> cir,
                                                                  Component message) {

        var data = new ChatReceivedClientCallback.Data(bound, message, Util.NIL_UUID);

        ChatReceivedClientCallback.EVENT.invoker().onChatReceived(data);

        if (data.isCanceled()) {
            cir.setReturnValue(false);
            return;
        }

        Component modified = data.getMessage();

        if (Objects.equals(message, modified)) return;

        // the message was modified by the event
        this.minecraft.gui.getChat().addMessage(modified);
        this.narrateChatMessage(bound, modified);
        this.logSystemMessage(component, instant);
        this.previousMessageTime = Util.getMillis();

        cir.setReturnValue(true);
    }
}
