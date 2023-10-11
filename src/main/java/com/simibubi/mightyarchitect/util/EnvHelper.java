package com.simibubi.mightyarchitect.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resources.language.LanguageManager;

import java.util.Locale;
import java.util.function.Supplier;

public class EnvHelper {

    private final boolean client;
    private final ArchitectInterface architectInterface;

    private EnvHelper() {
        client = tryIsClient();
        architectInterface = getArchitectInterface();
    }

    private static boolean tryIsClient() {
        // will probably not work on Quilt
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    private ArchitectInterface getArchitectInterface() {
        try {
            Class.forName("net.irisshaders.iris.api.v0.IrisApi");

            // Iris is installed, create Iris interface.
            return new IrisArchitectImpl();
        } catch (ClassNotFoundException ignored) {
            // Iris is not installed, create vanilla interface.
            return new VanillaArchitectImpl();
        }
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

    public static ArchitectInterface getInterface() {
        return Holder.INSTANCE.architectInterface;
    }
}
