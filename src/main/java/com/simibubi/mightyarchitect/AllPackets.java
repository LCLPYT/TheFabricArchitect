package com.simibubi.mightyarchitect;

import com.simibubi.mightyarchitect.networking.IPacketHandler;
import com.simibubi.mightyarchitect.networking.InstantPrintPacket;
import com.simibubi.mightyarchitect.networking.PlaceSignPacket;
import com.simibubi.mightyarchitect.networking.SetHotbarItemPacket;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class AllPackets {

	public static void registerPackets() {
		register(InstantPrintPacket.TYPE);
		register(PlaceSignPacket.TYPE);
		register(SetHotbarItemPacket.TYPE);
	}

	private static <T extends FabricPacket & IPacketHandler> void register(PacketType<T> type) {
		ServerPlayNetworking.registerGlobalReceiver(type, AllPackets::receive);
	}

	private static <T extends IPacketHandler> void receive(T packet, ServerPlayer player, PacketSender responseSender) {
		packet.handle(player, responseSender);
	}
}
