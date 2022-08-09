package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface KeyInputCallback {

    Event<KeyInputCallback> EVENT = EventFactory.createArrayBacked(KeyInputCallback.class,
            listeners -> ((key, scanCode, action, modifiers) -> {
                for (var listener : listeners) {
                    listener.onInput(key, scanCode, action, modifiers);
                }
            }));

    void onInput(int key, int scanCode, int action, int modifiers);
}
