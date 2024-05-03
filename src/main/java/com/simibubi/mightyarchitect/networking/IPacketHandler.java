package com.simibubi.mightyarchitect.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public interface IPacketHandler {

    void handle(ServerPlayNetworking.Context context);
}
