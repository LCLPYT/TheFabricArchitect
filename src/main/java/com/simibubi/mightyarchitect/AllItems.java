package com.simibubi.mightyarchitect;

import com.google.common.collect.ImmutableMap;
import com.simibubi.mightyarchitect.item.ArchitectWandItem;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

import static com.simibubi.mightyarchitect.TheFabricArchitect.rl;
import static net.minecraft.core.Registry.register;

public class AllItems {

	public static final Item ARCHITECT_WAND = new ArchitectWandItem(standardProperties());

	public static Properties standardProperties() {
		return new Properties();
	}

	public static void registerItems() {
		ImmutableMap.<Item, String>builder()
				.put(ARCHITECT_WAND, "architect_wand")
				.build()
				.forEach((item, path) -> register(Registry.ITEM, rl(path), item));
	}
}
