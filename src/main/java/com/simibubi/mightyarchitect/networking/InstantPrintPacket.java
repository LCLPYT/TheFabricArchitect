package com.simibubi.mightyarchitect.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

import static com.simibubi.mightyarchitect.TheFabricArchitect.rl;

public class InstantPrintPacket implements IPacket {

	public static final ResourceLocation ID = rl("instant_print");

	private final BunchOfBlocks blocks;

	public InstantPrintPacket(BunchOfBlocks blocks) {
		this.blocks = blocks;
	}

	public InstantPrintPacket(FriendlyByteBuf buf) {
		Map<BlockPos, BlockState> blocks = new HashMap<>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			CompoundTag blockTag = buf.readNbt();
			if (blockTag == null) continue;

			BlockPos pos = buf.readBlockPos();
			blocks.put(pos, NbtUtils.readBlockState(blockTag));
		}
		this.blocks = new BunchOfBlocks(blocks);
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(blocks.size);
		blocks.blocks.forEach((pos, state) -> {
			buf.writeNbt(NbtUtils.writeBlockState(state));
			buf.writeBlockPos(pos);
		});
	}
	
	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			final var commandSenderWorld = player.getCommandSenderWorld();
			blocks.blocks.forEach((pos, state) -> commandSenderWorld.setBlock(pos, state, 3));
		});
    }
	
	public static List<InstantPrintPacket> sendSchematic(Map<BlockPos, BlockState> blockMap, BlockPos anchor) {
		List<InstantPrintPacket> packets = new LinkedList<>();
		
		Map<BlockPos, BlockState> currentMap = new HashMap<>(BunchOfBlocks.MAX_SIZE);
		List<BlockPos> posList = new ArrayList<>(blockMap.keySet());
		
		for (int i = 0; i < blockMap.size(); i++) {
			if (currentMap.size() >= BunchOfBlocks.MAX_SIZE) {
				packets.add(new InstantPrintPacket(new BunchOfBlocks(currentMap)));
				currentMap = new HashMap<>(BunchOfBlocks.MAX_SIZE);
			}
			currentMap.put(posList.get(i).offset(anchor), blockMap.get(posList.get(i)));
		}
		packets.add(new InstantPrintPacket(new BunchOfBlocks(currentMap)));
		
		return packets;
	}

	public static void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
		new InstantPrintPacket(buf).handle(server, player);
	}

	static class BunchOfBlocks {
		static final int MAX_SIZE = 32;
		Map<BlockPos, BlockState> blocks;
		int size;
		
		public BunchOfBlocks(Map<BlockPos, BlockState> blocks) {
			this.blocks = blocks;
			this.size = blocks.size();
		}
		
	}

}
