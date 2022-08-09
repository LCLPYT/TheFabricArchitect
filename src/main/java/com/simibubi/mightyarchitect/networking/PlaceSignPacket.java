package com.simibubi.mightyarchitect.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

import static com.simibubi.mightyarchitect.TheFabricArchitect.rl;

public class PlaceSignPacket implements IPacket {

	public static final ResourceLocation ID = rl("place_sign");

	public String text1;
	public String text2;
	public BlockPos position;

	public PlaceSignPacket(String textLine1, String textLine2, BlockPos position) {
		this.text1 = textLine1;
		this.text2 = textLine2;
		this.position = position;
	}
	
	public PlaceSignPacket(FriendlyByteBuf buffer) {
		this(buffer.readUtf(128), buffer.readUtf(128), buffer.readBlockPos());
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeUtf(text1);
		buffer.writeUtf(text2);
		buffer.writeBlockPos(position);
	}
	
	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			var world = player.getCommandSenderWorld();
			world.setBlockAndUpdate(position, Blocks.SPRUCE_SIGN.defaultBlockState());

			final BlockEntity blockEntity = world.getBlockEntity(position);
			if (!(blockEntity instanceof SignBlockEntity sign)) return;

			sign.setMessage(0, new TextComponent(text1));
			sign.setMessage(1, new TextComponent(text2));
		});
	}

	public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
		new PlaceSignPacket(buf).handle(server, player);
	}
}
