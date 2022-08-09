package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MouseButtonPressedCallback {

    Event<MouseButtonPressedCallback> EVENT = EventFactory.createArrayBacked(MouseButtonPressedCallback.class,
            listeners -> ((button, modifiers) -> {
                for (var listener : listeners) {
                    listener.onInput(button, modifiers);
                }
            }));

    void onInput(int button, int modifiers);
}
