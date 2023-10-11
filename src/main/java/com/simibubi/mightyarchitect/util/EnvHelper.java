package com.simibubi.mightyarchitect.util;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.language.LanguageManager;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class EnvHelper {

    private final boolean client;

    private EnvHelper() {
        // will probably not work on Quilt
        client = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    private static class Holder {
        private static final EnvHelper INSTANCE = new EnvHelper();
    }

    public static boolean isClient() {
        return Holder.INSTANCE.client;
    }

    public static void onClient(Supplier<Runnable> runnable) {
        if (!isClient()) return;

        runnable.get().run();
    }

    public static Locale getJavaLocale(LanguageManager languageManager) {
        String selected = languageManager.getSelected();

        String[] langSplit = selected.split("_", 2);

        if (langSplit.length == 1) {
            return new Locale(langSplit[0]);
        }

        return new Locale(langSplit[0], langSplit[1]);
    }
}
