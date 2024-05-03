package com.simibubi.mightyarchitect.networking;

import com.simibubi.mightyarchitect.TheMightyArchitect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class SetHotbarItemPacket implements CustomPacketPayload, IPacketHandler {

	public static final Type<InstantPrintPacket> TYPE = new Type<>(TheMightyArchitect.asResource("instant_print"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetHotbarItemPacket> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, packet -> packet.slot,
			ItemStack.STREAM_CODEC, packet -> packet.stack,
			SetHotbarItemPacket::new);

	private int slot;
	private ItemStack stack;

	public SetHotbarItemPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handle(ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		player.getServer().execute(() -> {
			if (!player.isCreative())
				return;

			player.getInventory().setItem(slot, stack);
			//player.setSlot(slot, stack);
			player.inventoryMenu.broadcastChanges();
		});
	}

}
