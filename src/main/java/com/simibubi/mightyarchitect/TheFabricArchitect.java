package com.simibubi.mightyarchitect;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheFabricArchitect implements ModInitializer {

    public static final String MOD_ID = "mightyarchitect";
    public static final String NAME = "The Fabric Architect";

    public static Logger logger = LogManager.getLogger();

    @Override
    public void onInitialize() {
        AllPackets.registerPackets();
        AllItems.registerItems();
        AllBlocks.registerBlocks();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation rl(String format, Object... substitutes) {
        return rl(String.format(format, substitutes));
    }
}
