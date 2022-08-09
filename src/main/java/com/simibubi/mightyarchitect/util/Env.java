package com.simibubi.mightyarchitect.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import javax.annotation.Nonnull;

/**
 * Taken from MMOContent: https://github.com/LCLPYT/MMOContent/blob/d371af0a132aff43d3de126a251258332651e72b/src/main/java/work/lclpnet/mmocontent/util/Env.java
 * @author LCLP
 */
public class Env {

    private static EnvType currentEnv = null;

    @Nonnull
    public static EnvType currentEnv() {
        return currentEnv == null ? currentEnv = FabricLoader.getInstance().getEnvironmentType() : currentEnv;
    }

    public static boolean isClient() {
        return currentEnv() == EnvType.CLIENT;
    }

}
