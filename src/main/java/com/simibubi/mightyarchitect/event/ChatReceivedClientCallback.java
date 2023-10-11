package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public interface ChatReceivedClientCallback {

    Event<ChatReceivedClientCallback> EVENT = EventFactory.createArrayBacked(ChatReceivedClientCallback.class,
            listeners -> ((data) -> {
                for (var listener : listeners) {
                    listener.onChatReceived(data);
                }
            }));

    void onChatReceived(Data data);

    class Data {
        private final ChatType.Bound type;
        private Component message;
        private final UUID sender;
        private boolean canceled = false;

        public Data(ChatType.Bound type, Component message, UUID sender) {
            this.type = type;
            this.message = message;
            this.sender = sender;
        }

        public ChatType.Bound getType() {
            return type;
        }

        public Component getMessage() {
            return message;
        }

        public void setMessage(Component message) {
            this.message = message;
        }

        public UUID getSender() {
            return sender;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }
}
