package com.simibubi.mightyarchitect.control.phase;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.TheFabricArchitect;
import com.simibubi.mightyarchitect.control.ArchitectManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PrintingToMultiplayer extends PhaseBase {

	static List<BlockPos> remaining;
	static int cooldown;
	static boolean approved;

	@Override
	public void whenEntered() {
		remaining = new LinkedList<>(getModel().getMaterializedSketch().getAllPositions());
		remaining.sort(Comparator.comparingInt(Vec3i::getY));
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player != null)
			player.chat("/setblock checking permission for 'The Mighty Architect'.");
		cooldown = 500;
		approved = false;
	}

	@Override
	public void update() {
		if (minecraft.level == null || minecraft.player == null) return;

		if (cooldown > 0 && !approved) {
			cooldown--;
			return;
		}
		if (cooldown == 0) {
			ArchitectManager.enterPhase(ArchitectPhases.Previewing);
			return;
		}

		for (int i = 0; i < 10; i++) {
			if (!remaining.isEmpty()) {
				BlockPos pos = remaining.get(0);
				remaining.remove(0);
				pos = pos.offset(getModel().getAnchor());
				BlockState state = getModel().getMaterializedSketch().getBlockState(pos);

				if (minecraft.level.getBlockState(pos) == state)
					continue;

				if (!minecraft.level.isUnobstructed(state, pos, CollisionContext.of(minecraft.player)))
					continue;

				String blockstring = state.toString().replaceFirst("Block\\{", "").replaceFirst("}", "");

				final LocalPlayer player = Minecraft.getInstance().player;
				if (player != null)
					player.chat("/setblock " + pos.getX() + " " + pos.getY() + " "
							+ pos.getZ() + " " + blockstring);
			} else {
				ArchitectManager.unload();
				break;
			}
		}
	}

	public static Component onCommandFeedback(Component message, ChatType ignored, UUID ignored2) {
		if (message == null)
			return null;

		if (cooldown <= 0) return message;

		List<Component> checking = new LinkedList<>();
		checking.add(message);

		while (!checking.isEmpty()) {
			Component iTextComponent = checking.get(0);
			if (iTextComponent instanceof TranslatableComponent) {
				String test = ((TranslatableComponent) iTextComponent).getKey();

				TheFabricArchitect.logger.info(test);

				if (test.equals("command.unknown.command")) {
					cooldown = 0;
					return new TextComponent(ChatFormatting.RED + "You do not have permission to print on this server.");
				}
				if (test.equals("parsing.int.expected")) {
					approved = true;
					final LocalPlayer player = Minecraft.getInstance().player;
					if (player != null) {
						player.chat("/me is printing a structure created by the Mighty Architect.");
						player.chat("/gamerule sendCommandFeedback false");
						player.chat("/gamerule logAdminCommands false");
					}
					return null;  // cancel handling
				}
			} else {
				checking.addAll(iTextComponent.getSiblings());
			}
			checking.remove(iTextComponent);
		}

		return message;
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer) {
	}

	@Override
	public void whenExited() {
		if (approved) {
			final LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				player.displayClientMessage(new TextComponent(ChatFormatting.GREEN + "Finished Printing, enjoy!"),
						false);
				player.chat("/gamerule logAdminCommands true");
				player.chat("/gamerule sendCommandFeedback true");
			}
		}
		cooldown = 0;
	}

	@Override
	public List<String> getToolTip() {
		return ImmutableList.of("Please be patient while your building is being transferred.");
	}

}
