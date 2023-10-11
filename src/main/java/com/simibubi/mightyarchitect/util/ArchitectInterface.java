package com.simibubi.mightyarchitect.util;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ArchitectInterface {

    BufferBuilder createBufferBuilder();
}
