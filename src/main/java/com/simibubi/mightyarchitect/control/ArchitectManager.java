package com.simibubi.mightyarchitect.control;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.FabricArchitectClient;
import com.simibubi.mightyarchitect.control.compose.GroundPlan;
import com.simibubi.mightyarchitect.control.design.DesignExporter;
import com.simibubi.mightyarchitect.control.design.DesignTheme;
import com.simibubi.mightyarchitect.control.design.ThemeStorage;
import com.simibubi.mightyarchitect.control.palette.PaletteDefinition;
import com.simibubi.mightyarchitect.control.palette.PaletteStorage;
import com.simibubi.mightyarchitect.control.phase.ArchitectPhases;
import com.simibubi.mightyarchitect.control.phase.IArchitectPhase;
import com.simibubi.mightyarchitect.control.phase.IDrawBlockHighlights;
import com.simibubi.mightyarchitect.control.phase.IRenderGameOverlay;
import com.simibubi.mightyarchitect.event.KeyInputCallback;
import com.simibubi.mightyarchitect.event.MouseButtonPressedCallback;
import com.simibubi.mightyarchitect.event.MouseScrollCallback;
import com.simibubi.mightyarchitect.foundation.utility.FilesHelper;
import com.simibubi.mightyarchitect.foundation.utility.Keyboard;
import com.simibubi.mightyarchitect.gui.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Environment(EnvType.CLIENT)
public class ArchitectManager {

	private static ArchitectPhases phase = ArchitectPhases.Empty;
	private static Schematic model = new Schematic();
	private static final ArchitectMenuScreen menu = new ArchitectMenuScreen();

	public static boolean testRun = false;

	// Commands

	public static void compose() {
		enterPhase(ArchitectPhases.Composing);
	}

	public static void compose(DesignTheme theme) {
		if (getModel().isEmpty())
			getModel().setGroundPlan(new GroundPlan(theme));
		enterPhase(ArchitectPhases.Composing);
	}

	public static void pauseCompose() {
		status("Composer paused, use /compose to return.");
	}

	public static void unload() {
		if (!model.isEmpty())
			model.getTheme()
				.getDesignPicker()
				.reset();

		enterPhase(ArchitectPhases.Empty);
		resetSchematic();

		if (testRun) {
			testRun = false;
			editTheme(DesignExporter.theme);
			return;
		}

		menu.setVisible(false);
	}

	public static void design() {
		GroundPlan groundPlan = model.getGroundPlan();

		if (groundPlan.isEmpty()) {
			status("Draw some rooms before going to the next step!");
			return;
		}

		model.setSketch(groundPlan.theme.getDesignPicker()
			.assembleSketch(groundPlan, model.seed));
		enterPhase(ArchitectPhases.Previewing);
	}

	public static void reAssemble() {
		GroundPlan groundPlan = model.getGroundPlan();
		model.setSketch(groundPlan.theme.getDesignPicker()
			.assembleSketch(groundPlan, model.seed));
		FabricArchitectClient.renderer.update();
	}

	public static void createPalette(boolean primary) {
		getModel().startCreatingNewPalette(primary);
		enterPhase(ArchitectPhases.CreatingPalette);
	}

	public static void finishPalette(String name) {
		if (name.isEmpty())
			name = "My Palette";

		PaletteDefinition palette = getModel().getCreatedPalette();
		palette.setName(name);
		PaletteStorage.exportPalette(palette);
		PaletteStorage.loadAllPalettes();

		getModel().applyCreatedPalette();
		status("Your new palette has been saved.");
		enterPhase(ArchitectPhases.Previewing);
	}

	public static void print() {
		if (getModel().getSketch() == null)
			return;

		Minecraft mc = Minecraft.getInstance();

		if (mc.hasSingleplayerServer()) {
			getModel().getPackets().forEach(FabricArchitectClient::sendToServer);
			FabricArchitectClient.renderer.setActive(false);
			status("Printed result into world.");
			unload();
			return;
		}

		enterPhase(ArchitectPhases.PrintingToMultiplayer);
	}

	public static void writeToFile(String name) {
		if (getModel().getSketch() == null)
			return;

		if (name.isEmpty())
			name = "My Build";

		String folderPath = "schematics";

		FilesHelper.createFolderIfMissing(folderPath);
		String filename = FilesHelper.findFirstValidFilename(name, folderPath, "nbt");
		String filepath = folderPath + "/" + filename;

		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(Paths.get(filepath), StandardOpenOption.CREATE);
			CompoundTag nbttagcompound = getModel().writeToTemplate()
				.save(new CompoundTag());
			NbtIo.writeCompressed(nbttagcompound, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				IOUtils.closeQuietly(outputStream);
		}
		status("Saved as " + filepath);

		BlockPos pos = model.getAnchor()
			.offset(model.getMaterializedSketch().getBounds()
				.getOrigin());
		TextComponent component = new TextComponent("Deploy Schematic at: " + ChatFormatting.BLUE + "["
			+ pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]");
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player != null)
			player.displayClientMessage(component, false);
		unload();
	}

	public static void status(String message) {
		final LocalPlayer player = Minecraft.getInstance().player;
		if (player != null)
			player.displayClientMessage(new TextComponent(message), true);
	}

	public static void pickPalette() {
		if (getModel().getSketch() == null)
			return;

		if (inPhase(ArchitectPhases.CreatingPalette)) {
			getModel().stopPalettePreview();
			enterPhase(ArchitectPhases.Previewing);
		}

		ScreenHelper.open(new PalettePickerScreen());
	}

	public static void pickScanPalette() {
		ScreenHelper.open(new PalettePickerScreen(true));
	}

	public static void manageThemes() {
		enterPhase(ArchitectPhases.ManagingThemes);
	}

	public static void createTheme() {
		TextInputPromptScreen gui = new TextInputPromptScreen(result -> {
			DesignExporter.setTheme(ThemeStorage.createTheme(result));
			ScreenHelper.open(new ThemeSettingsScreen());
		}, result -> {
		});
		gui.setButtonTextConfirm("Create");
		gui.setButtonTextAbort("Cancel");
		gui.setTitle("Enter a name for your Theme:");

		ScreenHelper.open(gui);
	}

	public static void editTheme(DesignTheme theme) {
		DesignExporter.setTheme(theme);
		enterPhase(ArchitectPhases.EditingThemes);
	}

	public static void changeExportedDesign() {
		ScreenHelper.open(new DesignExporterScreen());
	}

	// Phases

	public static boolean inPhase(ArchitectPhases phase) {
		return ArchitectManager.phase == phase;
	}

	public static void enterPhase(ArchitectPhases newPhase) {
		IArchitectPhase phaseHandler = phase.getPhaseHandler();
		phaseHandler.whenExited();
		phaseHandler = newPhase.getPhaseHandler();
		phaseHandler.whenEntered();
		phase = newPhase;
		menu.updateContents();
	}

	public static Schematic getModel() {
		return model;
	}

	public static ArchitectPhases getPhase() {
		return phase;
	}

	// Events

	public static void registerEvents() {
		ClientTickEvents.START_CLIENT_TICK.register(ArchitectManager::onClientTick);
		MouseScrollCallback.EVENT.register(ArchitectManager::onMouseScrolled);
		MouseButtonPressedCallback.EVENT.register(ArchitectManager::onClick);
		KeyInputCallback.EVENT.register(ArchitectManager::onKeyTyped);
		HudRenderCallback.EVENT.register(ArchitectManager::onDrawGameOverlay);
	}

	public static void onClientTick(Minecraft mc) {
		if (Minecraft.getInstance().level == null) {
			if (!inPhase(ArchitectPhases.Paused) && !model.isEmpty())
				enterPhase(ArchitectPhases.Paused);
			return;
		}

		phase.getPhaseHandler()
			.update();
		menu.onClientTick();

	}

	public static boolean onMouseScrolled(MouseHandler handler, double amount) {
		if (Minecraft.getInstance().screen != null)
			return false;

		return phase.getPhaseHandler()
				.onScroll((int) Math.signum(amount));
	}

	public static void render(PoseStack ms, MultiBufferSource buffer) {
		if (Minecraft.getInstance().level != null)
			phase.getPhaseHandler()
				.render(ms, buffer);
	}

	public static void onClick(int button, int modifiers) {
		if (Minecraft.getInstance().screen != null)
			return;

		phase.getPhaseHandler()
			.onClick(button);
	}

	public static void onKeyTyped(int key, int scanCode, int action, int modifiers) {
		if (key == GLFW.GLFW_KEY_ESCAPE && action == Keyboard.PRESS) {
			if (inPhase(ArchitectPhases.Composing) || inPhase(ArchitectPhases.Previewing)) {
				enterPhase(ArchitectPhases.Paused);
				menu.setVisible(false);
			}
			return;
		}
		if (Minecraft.getInstance().screen != null)
			return;
		if (FabricArchitectClient.COMPOSE.consumeClick()) {
			if (!menu.isFocused())
				openMenu();
			return;
		}

		boolean released = action == Keyboard.RELEASE;
		phase.getPhaseHandler()
			.onKey(key, released);
	}

	public static void openMenu() {
		menu.updateContents();
		ScreenHelper.open(menu);
		menu.setFocused(true);
		menu.setVisible(true);
	}

	public static void tickBlockHighlightOutlines() {
		IArchitectPhase phaseHandler = phase.getPhaseHandler();
		if (phaseHandler instanceof IDrawBlockHighlights)
			((IDrawBlockHighlights) phaseHandler).tickHighlightOutlines();
	}

	public static void onDrawGameOverlay(PoseStack stack, float partialTicks) {
		IArchitectPhase phaseHandler = phase.getPhaseHandler();
		if (phaseHandler instanceof IRenderGameOverlay) {
			((IRenderGameOverlay) phaseHandler).renderGameOverlay(stack, partialTicks);
		}

		menu.drawPassive();
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
	}

	public static void resetSchematic() {
		model = new Schematic();
	}

}
