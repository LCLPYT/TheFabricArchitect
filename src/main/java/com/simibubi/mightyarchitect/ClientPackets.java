package com.simibubi.mightyarchitect;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
public class ClientPackets {

    public static boolean isRemotePresent(CustomPacketPayload.Type<? extends CustomPacketPayload> type) {
        return ClientPlayNetworking.canSend(type);
    }
}
