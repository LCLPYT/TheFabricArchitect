package com.simibubi.mightyarchitect.foundation.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;

import java.text.NumberFormat;
import java.util.Locale;

public class LangNumberFormat {

	private NumberFormat format = NumberFormat.getNumberInstance(Locale.ROOT);
	public static LangNumberFormat numberFormat = new LangNumberFormat();

	public NumberFormat get() {
		return format;
	}

	public void update() {
		final LanguageInfo selected = Minecraft.getInstance()
				.getLanguageManager()
				.getSelected();
		format = NumberFormat.getInstance(((LanguageInfoDuck) selected).mightyarchitect$getJavaLocale());
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(0);
		format.setGroupingUsed(true);
	}

	public static String format(double d) {
		return numberFormat.get()
			.format(d)
			.replace("\u00A0", " ");
	}

}