package com.simibubi.mightyarchitect.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class NbtUtil {

    private NbtUtil() {}

    public static BlockPos readBlockPos(CompoundTag nbt) {
        return new BlockPos(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
    }
}
