package com.simibubi.mightyarchitect.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import static com.simibubi.mightyarchitect.TheFabricArchitect.rl;

public class SetHotbarItemPacket implements IPacket {

	public static final ResourceLocation ID = rl("set_hotbar_item");

	private final int slot;
	private final ItemStack stack;

	public SetHotbarItemPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}
	
	public SetHotbarItemPacket(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readItem());
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
		buffer.writeItem(stack);
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			if (!player.isCreative())
				return;

			player.getInventory().setItem(slot, stack);
			player.inventoryMenu.broadcastChanges();
		});
	}

	public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
		new SetHotbarItemPacket(buf).handle(server, player);
	}
}
