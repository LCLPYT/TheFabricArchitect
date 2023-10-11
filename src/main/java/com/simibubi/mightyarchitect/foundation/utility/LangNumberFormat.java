package com.simibubi.mightyarchitect.foundation.utility;

import java.text.NumberFormat;
import java.util.Locale;

import com.simibubi.mightyarchitect.util.EnvHelper;
import net.minecraft.client.Minecraft;

public class LangNumberFormat {

	private NumberFormat format = NumberFormat.getNumberInstance(Locale.ROOT);
	public static LangNumberFormat numberFormat = new LangNumberFormat();

	public NumberFormat get() {
		return format;
	}

	public void update() {
		format = NumberFormat.getInstance(EnvHelper.getJavaLocale(Minecraft.getInstance()
			.getLanguageManager()));
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