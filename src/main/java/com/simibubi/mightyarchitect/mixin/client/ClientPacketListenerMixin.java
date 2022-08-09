package com.simibubi.mightyarchitect.mixin.client;

import com.simibubi.mightyarchitect.event.ChatReceivedClientCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "handleChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;handleChat(Lnet/minecraft/network/chat/ChatType;Lnet/minecraft/network/chat/Component;Ljava/util/UUID;)V"
            ),
            cancellable = true
    )
    public void onChat(ClientboundChatPacket packet, CallbackInfo ci) {
        final Component original = packet.getMessage().copy();
        final Component modified = ChatReceivedClientCallback.EVENT.invoker().onChatReceived(packet.getMessage(), packet.getType(), packet.getSender());
        if (!original.equals(modified)) return;

        ci.cancel();
        this.minecraft.gui.handleChat(packet.getType(), modified, packet.getSender());
    }
}
