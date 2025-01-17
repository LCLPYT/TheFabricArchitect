package com.simibubi.mightyarchitect.foundation.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ShaderManager {

	private static Shaders activeShader = Shaders.None;

	public static void onClientTick(Minecraft minecraft) {
		if (Minecraft.getInstance().level == null && activeShader != Shaders.None)
			stopUsingShaders();
		
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;
		MobEffectInstance activePotionEffect = player.getEffect(MobEffects.NIGHT_VISION);

		if (activeShader == Shaders.Blueprint) {
			if (activePotionEffect == null || activePotionEffect.getDuration() < 999)
				player.addEffect(new NVEffectInstance());
			return;
		}
		
		if (activePotionEffect instanceof NVEffectInstance) 
			player.removeEffectNoUpdate(MobEffects.NIGHT_VISION);
	}

	public static Shaders getActiveShader() {
		return activeShader;
	}

	public static void setActiveShader(Shaders activeShader) {
		if (getActiveShader() == activeShader)
			return;
		ShaderManager.activeShader = activeShader;
		activeShader.setActive(true);
	}

	public static void stopUsingShaders() {
		activeShader = Shaders.None;
		activeShader.setActive(true);
	}

	private static class NVEffectInstance extends MobEffectInstance {

		public NVEffectInstance() {
			super(MobEffects.NIGHT_VISION, 1000, 0, false, false, false);
		}

	}

}
