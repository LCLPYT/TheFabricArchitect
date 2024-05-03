package com.simibubi.mightyarchitect.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.vertices.IrisVertexFormats;

/**
 * Interface for shader support via Iris.
 */
@Environment(EnvType.CLIENT)
public class IrisArchitectImpl implements ArchitectInterface {

    @Override
    public BufferBuilder createBufferBuilder() {
        VertexFormat format;

        if (IrisApi.getInstance().isShaderPackInUse()) {
            format = IrisVertexFormats.TERRAIN;
        } else {
            format = DefaultVertexFormat.BLOCK;
        }

        return new BufferBuilder(format.getIntegerSize());
    }
}
