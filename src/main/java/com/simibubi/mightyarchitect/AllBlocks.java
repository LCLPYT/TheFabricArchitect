package com.simibubi.mightyarchitect;

import com.google.common.collect.ImmutableMap;
import com.simibubi.mightyarchitect.block.DesignAnchorBlock;
import com.simibubi.mightyarchitect.block.IJustForRendering;
import com.simibubi.mightyarchitect.block.SliceMarkerBlock;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import static com.simibubi.mightyarchitect.TheFabricArchitect.rl;
import static net.minecraft.core.Registry.register;

public class AllBlocks {

	public static final Block SLICE_MARKER = new SliceMarkerBlock();
	public static final Block DESIGN_ANCHOR = new DesignAnchorBlock();

	public static void registerBlocks() {
		ImmutableMap.<Block, String>builder()
				.put(SLICE_MARKER, "slice_marker")
				.put(DESIGN_ANCHOR, "design_anchor")
				.build()
				.forEach((block, path) -> {
					final var rl = rl(path);
					register(Registry.BLOCK, rl, block);

					if (block instanceof IJustForRendering) return;

					var item = new BlockItem(block, AllItems.standardProperties());
					register(Registry.ITEM, rl, item);
				});
	}
}
