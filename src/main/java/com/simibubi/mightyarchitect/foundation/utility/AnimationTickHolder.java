package com.simibubi.mightyarchitect.foundation.utility;

import net.minecraft.client.Minecraft;

public class AnimationTickHolder {

	public static int ticks;

	public static void tick() {
		ticks++;
	}

	public static float getRenderTick() {
		return ticks + Minecraft.getInstance().getRenderPartialTicks();
	}

}
