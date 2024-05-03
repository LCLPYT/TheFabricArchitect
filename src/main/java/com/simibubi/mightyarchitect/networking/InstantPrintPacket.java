package com.simibubi.mightyarchitect.networking;

import com.simibubi.mightyarchitect.TheMightyArchitect;
import com.simibubi.mightyarchitect.foundation.utility.BlockHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class InstantPrintPacket implements CustomPacketPayload, IPacketHandler {

	public static final Type<InstantPrintPacket> TYPE = new Type<>(TheMightyArchitect.asResource("instant_print"));

	public static final StreamCodec<FriendlyByteBuf, InstantPrintPacket> CODEC = StreamCodec.ofMember(InstantPrintPacket::write, InstantPrintPacket::new);

	private BunchOfBlocks blocks;

	public InstantPrintPacket() {}

	public InstantPrintPacket(BunchOfBlocks blocks) {
		this.blocks = blocks;
	}

	public InstantPrintPacket(FriendlyByteBuf buf) {
		Map<BlockPos, BlockState> blocks = new HashMap<>();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			CompoundTag blockTag = buf.readNbt();
			BlockPos pos = buf.readBlockPos();
			blocks.put(pos, NbtUtils.readBlockState(BlockHelper.lookup(), blockTag));
		}
		this.blocks = new BunchOfBlocks(blocks);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(blocks.size);
		blocks.blocks.forEach((pos, state) -> {
			buf.writeNbt(NbtUtils.writeBlockState(state));
			buf.writeBlockPos(pos);
		});
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handle(ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		if (!player
			.hasPermissions(2))
			return;
		player.getServer()
			.execute(() -> {
				blocks.blocks.forEach((pos, state) -> {
					player
						.getCommandSenderWorld()
						.setBlock(pos, state, 3);
				});
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
			currentMap.put(posList.get(i)
				.offset(anchor), blockMap.get(posList.get(i)));
		}
		packets.add(new InstantPrintPacket(new BunchOfBlocks(currentMap)));

		return packets;
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
