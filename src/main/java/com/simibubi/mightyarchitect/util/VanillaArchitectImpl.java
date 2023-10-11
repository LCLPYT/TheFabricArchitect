package com.simibubi.mightyarchitect.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VanillaArchitectImpl implements ArchitectInterface {

    @Override
    public BufferBuilder createBufferBuilder() {
        return new BufferBuilder(DefaultVertexFormat.BLOCK.getIntegerSize());
    }
}
