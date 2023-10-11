package com.simibubi.mightyarchitect.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.level.ServerPlayer;

public interface IPacketHandler {

    void handle(ServerPlayer player, PacketSender responseSender);
}
