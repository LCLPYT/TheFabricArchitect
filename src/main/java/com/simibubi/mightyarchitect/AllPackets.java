package com.simibubi.mightyarchitect;

import com.simibubi.mightyarchitect.networking.InstantPrintPacket;
import com.simibubi.mightyarchitect.networking.PlaceSignPacket;
import com.simibubi.mightyarchitect.networking.SetHotbarItemPacket;

import static net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver;

public class AllPackets {

	public static void registerPackets() {
		registerGlobalReceiver(InstantPrintPacket.ID, InstantPrintPacket::receive);
		registerGlobalReceiver(PlaceSignPacket.ID, PlaceSignPacket::receive);
		registerGlobalReceiver(SetHotbarItemPacket.ID, SetHotbarItemPacket::receive);
	}
}
