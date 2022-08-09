package com.simibubi.mightyarchitect.control.phase;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.FabricArchitectClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class PhasePaused extends PhaseBase {

	@Override
	public void whenEntered() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null)
			return;
		player.displayClientMessage(new TextComponent(
			"The Mighty Architect was " + ChatFormatting.BOLD + "Paused" + ChatFormatting.RESET + "."), false);
		player.displayClientMessage(new TextComponent("You can continue composing with [" + ChatFormatting.AQUA
			+ FabricArchitectClient.COMPOSE.getTranslatedKeyMessage()
				.getString()
				.toUpperCase()
			+ ChatFormatting.WHITE + "]"), false);
	}

	@Override
	public void update() {

	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer) {

	}

	@Override
	public void whenExited() {

	}

	@Override
	public List<String> getToolTip() {
		return ImmutableList.of("You have started a build earlier, would you like to continue where you left off?");
	}

}
