package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class MouseButtonEvents {

    public static final Event<MouseCallback> PRE = EventFactory.createArrayBacked(MouseCallback.class,
            listeners -> ((button, action, modifiers) -> {
                for (var listener : listeners) {
                    listener.onInput(button, action, modifiers);
                }
            }));

    public static final Event<MouseCallback> POST = EventFactory.createArrayBacked(MouseCallback.class,
            listeners -> ((button, action, modifiers) -> {
                for (var listener : listeners) {
                    listener.onInput(button, action, modifiers);
                }
            }));

    public interface MouseCallback {
        void onInput(int button, int action, int modifiers);
    }
}
