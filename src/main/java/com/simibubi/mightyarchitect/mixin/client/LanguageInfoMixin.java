package com.simibubi.mightyarchitect.mixin.client;

import com.simibubi.mightyarchitect.foundation.utility.LanguageInfoDuck;
import net.minecraft.client.resources.language.LanguageInfo;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(LanguageInfo.class)
public class LanguageInfoMixin implements LanguageInfoDuck {

    @Shadow @Final private String code;

    @Mutable @Unique @Final private Locale javaLocale;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void onConstruct(String code, String region, String name, boolean bidirectional, CallbackInfo ci) {
        String[] splitLangCode = code.split("_", 2);
        if (splitLangCode.length == 1) {
            this.javaLocale = new Locale(this.code);
        } else {
            this.javaLocale = new Locale(splitLangCode[0], splitLangCode[1]);
        }
    }

    @Override
    public Locale mightyarchitect$getJavaLocale() {
        return javaLocale;
    }
}
