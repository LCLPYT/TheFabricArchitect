package com.simibubi.mightyarchitect.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.simibubi.mightyarchitect.FabricArchitectClient;
import net.coderbot.iris.vertices.IrisVertexFormats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.api.v0.IrisApi;

/**
 * This class serves as a bridge to the Iris shaders mod.
 * Consumers shall not load this class, unless Iris is installed.
 * If Iris is installed, consumers can use {@link FabricArchitectClient#getInterface()} as getter.
 */
@Environment(EnvType.CLIENT)
public class IrisArchitectImpl implements ArchitectInterface {

    @Override
    public BufferBuilder createBufferBuilder() {
        final var format = IrisApi.getInstance().isShaderPackInUse()
                ? IrisVertexFormats.TERRAIN : DefaultVertexFormat.BLOCK;
        return new BufferBuilder(format.getIntegerSize());
    }
}
