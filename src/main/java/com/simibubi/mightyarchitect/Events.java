package com.simibubi.mightyarchitect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.control.ArchitectManager;

import com.simibubi.mightyarchitect.control.phase.PrintingToMultiplayer;
import com.simibubi.mightyarchitect.event.ChatReceivedClientCallback;
import com.simibubi.mightyarchitect.event.KeyInputCallback;
import com.simibubi.mightyarchitect.event.LevelRenderLastCallback;
import com.simibubi.mightyarchitect.event.MouseButtonEvents;
import com.simibubi.mightyarchitect.foundation.utility.ShaderManager;
import com.simibubi.mightyarchitect.gui.ScreenHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class Events {

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(Events::onTick);
		KeyInputCallback.EVENT.register(Events::keyPress);
		MouseButtonEvents.PRE.register(Events::mouseButtonPress);
		MouseButtonEvents.POST.register(Events::mouseButtonPress);
		LevelRenderLastCallback.EVENT.register(Events::onRenderWorld);
		ClientTickEvents.START_CLIENT_TICK.register(ShaderManager::onClientTick);
		ClientTickEvents.START_CLIENT_TICK.register(ScreenHelper::onClientTick);
		ChatReceivedClientCallback.EVENT.register(PrintingToMultiplayer::onCommandFeedback);
	}

	public static void onTick(Minecraft client) {
		if (isInLevel())
			MightyClient.tick();
	}

	public static void keyPress(int key, int scanCode, int action, int modifiers) {
		if (isInLevel() && !isInGUI())
			Keybinds.handleKey(key, scanCode, action, modifiers);
	}

	public static void mouseButtonPress(int button, int action, int modifiers) {
		if (isInLevel() && !isInGUI())
			Keybinds.handleMouseButton(button, action, modifiers);
	}

	public static void onRenderWorld(PoseStack ms, float partialTicks, Camera camera) {
		ms.pushPose();
		BufferSource buffer = Minecraft.getInstance()
				.renderBuffers()
				.bufferSource();

		Vec3 cameraPosition = camera
				.getPosition();

		MightyClient.renderer.render(ms, buffer, cameraPosition);
		ArchitectManager.render(ms, buffer, cameraPosition);
		MightyClient.outliner.renderOutlines(ms, buffer, cameraPosition, partialTicks);

		buffer.endLastBatch();
		RenderSystem.enableCull();
		ms.popPose();
	}

	protected static boolean isInLevel() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

	protected static boolean isInGUI() {
		return Minecraft.getInstance().screen != null;
	}
}