package com.simibubi.mightyarchitect.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;

public interface LevelRenderLastCallback {

    Event<LevelRenderLastCallback> EVENT = EventFactory.createArrayBacked(LevelRenderLastCallback.class,
            listeners -> ((stack, partialTicks, camera) -> {
                for (var listener : listeners) {
                    listener.onLast(stack, partialTicks, camera);
                }
            }));

    void onLast(PoseStack stack, float partialTicks, Camera camera);
}
