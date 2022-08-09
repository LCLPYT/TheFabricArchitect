package com.simibubi.mightyarchitect.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket {

    ResourceLocation getId();

    void toBytes(FriendlyByteBuf buf);
}
