package com.simibubi.mightyarchitect;

import com.simibubi.mightyarchitect.networking.InstantPrintPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;

@Environment(EnvType.CLIENT)
public class ClientPackets {

    public static boolean isRemotePresent(PacketType<?> packetType) {
        return ClientPlayNetworking.canSend(packetType);
    }

    public static void sendToServer(FabricPacket packet) {
        ClientPlayNetworking.send(packet);
    }
}
