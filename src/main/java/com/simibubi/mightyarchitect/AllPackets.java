package com.simibubi.mightyarchitect;

import com.simibubi.mightyarchitect.networking.IPacketHandler;
import com.simibubi.mightyarchitect.networking.InstantPrintPacket;
import com.simibubi.mightyarchitect.networking.PlaceSignPacket;
import com.simibubi.mightyarchitect.networking.SetHotbarItemPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class AllPackets {

	public static void registerPackets() {
		var playC2S = PayloadTypeRegistry.playC2S();
		playC2S.register(InstantPrintPacket.TYPE, InstantPrintPacket.CODEC);

		register(InstantPrintPacket.TYPE);
		register(PlaceSignPacket.TYPE);
		register(SetHotbarItemPacket.TYPE);
	}

	private static <T extends CustomPacketPayload & IPacketHandler> void register(CustomPacketPayload.Type<T> type) {
		ServerPlayNetworking.registerGlobalReceiver(type, AllPackets::receive);
	}

	private static <T extends IPacketHandler> void receive(T packet, ServerPlayNetworking.Context context) {
		packet.handle(context);
	}
}
