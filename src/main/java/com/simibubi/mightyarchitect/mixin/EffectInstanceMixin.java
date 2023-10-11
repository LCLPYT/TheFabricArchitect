package com.simibubi.mightyarchitect.mixin;

import net.minecraft.client.renderer.EffectInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin {

    @Shadow @Final private static String EFFECT_SHADER_PATH;

    /**
     * Support shader effects from other namespaces than minecraft:path.
     * @param orig The original argument.
     * @return The modified argument.
     */
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"
            ),
            index = 0
    )
    private String mightyarchitect$correctRLConstructor(final String orig) {
        return fixResourceLocation(orig);
    }

    /**
     * Support shader effects from other namespaces than minecraft:path.
     * @param orig The original argument.
     * @return The modified argument.
     */
    @ModifyArg(
            method = "getOrCreate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resources/ResourceLocation;<init>(Ljava/lang/String;)V"
            ),
            index = 0
    )
    private static String mightyarchitect$correctRLGetOrCreate(final String orig) {
        return fixResourceLocation(orig);
    }

    @Unique
    private static String fixResourceLocation(final String orig) {
        // check if the path was already modified
        if (!orig.startsWith(EFFECT_SHADER_PATH)) return orig;

        final var nameWithExt = orig.substring(EFFECT_SHADER_PATH.length());
        final var split = nameWithExt.split(":");
        if (split.length != 2) return orig;

        return split[0] + ':' + EFFECT_SHADER_PATH + split[1];
    }
}
