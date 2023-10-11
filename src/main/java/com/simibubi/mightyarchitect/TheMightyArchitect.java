package com.simibubi.mightyarchitect;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;

public class TheMightyArchitect implements ModInitializer {

	public static final String ID = "mightyarchitect";
	public static final String NAME = "The Mighty Architect";
	public static final String VERSION = "0.6";

	public static TheMightyArchitect instance;
	public static Logger logger = LogManager.getLogger();

	@Override
	public void onInitialize() {
		AllPackets.registerPackets();
		AllBlocks.registerBlocks();
		AllItems.registerItems();
		AllBlocks.registerItems();
	}
	
	public static ResourceLocation asResource(String path) {
		return new ResourceLocation(ID, path);
	}
}