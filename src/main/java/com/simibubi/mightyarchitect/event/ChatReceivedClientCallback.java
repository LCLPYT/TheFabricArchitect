package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public interface ChatReceivedClientCallback {

    Event<ChatReceivedClientCallback> EVENT = EventFactory.createArrayBacked(ChatReceivedClientCallback.class,
            listeners -> ((message, type, sender) -> {
                for (var listener : listeners) {
                    message = listener.onChatReceived(message, type, sender);
                    if (message == null) return null;
                }

                return message;
            }));

    Component onChatReceived(Component message, ChatType type, UUID sender);
}
