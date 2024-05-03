package com.simibubi.mightyarchitect.networking;

import com.simibubi.mightyarchitect.TheMightyArchitect;
import com.simibubi.mightyarchitect.foundation.utility.Lang;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

public class PlaceSignPacket implements CustomPacketPayload, IPacketHandler {

	public static final Type<InstantPrintPacket> TYPE = new Type<>(TheMightyArchitect.asResource("instant_print"));

	public static final StreamCodec<FriendlyByteBuf, PlaceSignPacket> CODEC = StreamCodec.ofMember(PlaceSignPacket::write, PlaceSignPacket::new);

	public String text1;
	public String text2;
	public BlockPos position;

	public PlaceSignPacket() {}

	public PlaceSignPacket(String textLine1, String textLine2, BlockPos position) {
		this.text1 = textLine1;
		this.text2 = textLine2;
		this.position = position;
	}

	public PlaceSignPacket(FriendlyByteBuf buffer) {
		this(buffer.readUtf(128), buffer.readUtf(128), buffer.readBlockPos());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(text1);
		buffer.writeUtf(text2);
		buffer.writeBlockPos(position);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@Override
	public void handle(ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		player.getServer()
			.execute(() -> {
				Level entityWorld = player
					.getCommandSenderWorld();
				entityWorld.setBlockAndUpdate(position, Blocks.SPRUCE_SIGN.defaultBlockState());
				SignBlockEntity sign = (SignBlockEntity) entityWorld.getBlockEntity(position);
				SignText pText = new SignText();
				pText.setMessage(0, Lang.text(text1)
					.component());
				pText.setMessage(1, Lang.text(text2)
					.component());
				sign.setText(pText, true);
			});
	}

}
