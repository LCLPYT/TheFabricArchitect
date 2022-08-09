package com.simibubi.mightyarchitect.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MouseHandler;

public interface MouseScrollCallback {

    Event<MouseScrollCallback> EVENT = EventFactory.createArrayBacked(MouseScrollCallback.class,
            listeners -> ((handler, scrollAmount) -> {
                for (var listener : listeners) {
                    if (listener.onScroll(handler, scrollAmount))
                        return true;
                }

                return false;
            }));

    boolean onScroll(MouseHandler handler, double scrollAmount);
}
