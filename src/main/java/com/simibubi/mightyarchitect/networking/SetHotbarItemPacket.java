package com.simibubi.mightyarchitect.networking;

import java.util.function.Supplier;

import com.simibubi.mightyarchitect.TheMightyArchitect;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public class SetHotbarItemPacket implements FabricPacket, IPacketHandler {

	public static final PacketType<InstantPrintPacket> TYPE = PacketType.create(
			TheMightyArchitect.asResource("instant_print"), InstantPrintPacket::new);

	private int slot;
	private ItemStack stack;

	public SetHotbarItemPacket(int slot, ItemStack stack) {
		this.slot = slot;
		this.stack = stack;
	}
	
	public SetHotbarItemPacket(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readItem());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
		buffer.writeItem(stack);
	}

	@Override
	public PacketType<?> getType() {
		return TYPE;
	}

	@Override
	public void handle(ServerPlayer player, PacketSender responseSender) {
		player.getServer().execute(() -> {
			if (!player.isCreative())
				return;

			player.getInventory().setItem(slot, stack);
			//player.setSlot(slot, stack);
			player.inventoryMenu.broadcastChanges();
		});
	}

}
