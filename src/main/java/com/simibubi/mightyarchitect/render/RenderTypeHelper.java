package com.simibubi.mightyarchitect.render;

import com.simibubi.mightyarchitect.util.PredicateIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RenderTypeHelper {

    @NotNull
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull)
    {
        if (chunkRenderType != RenderType.translucent()) {
            return Sheets.cutoutBlockSheet();
        }

        if (cull || !Minecraft.useShaderTransparency()) {
            return Sheets.translucentCullBlockSheet();
        }

        return Sheets.translucentItemSheet();
    }

    public static Iterable<RenderType> getRenderTypes(BlockState state) {
        var parent = RenderType.chunkBufferLayers().iterator();

        return () -> new PredicateIterator<>(parent,
                renderType -> renderType.equals(ItemBlockRenderTypes.getChunkRenderType(state)));
    }
}
