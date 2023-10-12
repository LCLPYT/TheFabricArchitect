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
        private Component message;
        private boolean canceled = false;

        public Data(Component message) {
            this.message = message;
        }

        public Component getMessage() {
            return message;
        }

        public void setMessage(Component message) {
            this.message = message;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public void setCanceled(boolean canceled) {
            this.canceled = canceled;
        }
    }
}
