package com.simibubi.mightyarchitect.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface LevelRenderLastCallback {

    Event<LevelRenderLastCallback> EVENT = EventFactory.createArrayBacked(LevelRenderLastCallback.class,
            listeners -> ((stack, partialTicks) -> {
                for (var listener : listeners) {
                    listener.onLast(stack, partialTicks);
                }
            }));

    void onLast(PoseStack stack, float partialTicks);
}
