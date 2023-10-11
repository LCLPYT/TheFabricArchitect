package com.simibubi.mightyarchitect;

import java.util.Locale;
import java.util.function.Supplier;

import com.simibubi.mightyarchitect.block.DesignAnchorBlock;
import com.simibubi.mightyarchitect.block.IJustForRendering;
import com.simibubi.mightyarchitect.block.SliceMarkerBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public enum AllBlocks {

	SLICE_MARKER(() -> new SliceMarkerBlock()), DESIGN_ANCHOR(() -> new DesignAnchorBlock());

	private Supplier<Block> factory;
	private Block block;

	public ResourceLocation id;

	private AllBlocks(Supplier<Block> factory) {
		this.factory = factory;
		this.id = TheMightyArchitect.asResource(name().toLowerCase(Locale.ROOT));
	}

	public static void registerBlocks() {
		for (AllBlocks block : values()) {
			Registry.register(BuiltInRegistries.BLOCK, block.id, block.get());
		}
	}

	public static void registerItems() {
		for (AllBlocks block : values()) {
			if (block.get() instanceof IJustForRendering) continue;

			Registry.register(BuiltInRegistries.ITEM, block.id, new BlockItem(block.get(), AllItems.standardProperties()));
		}
	}

	public Block get() {
		if (block == null)
			block = factory.get();
		return block;
	}

	public boolean typeOf(BlockState state) {
		return state.getBlock() == block;
	}

}
