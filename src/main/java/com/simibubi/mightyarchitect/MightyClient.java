package com.simibubi.mightyarchitect;

import com.simibubi.mightyarchitect.control.ArchitectManager;
import com.simibubi.mightyarchitect.control.SchematicRenderer;
import com.simibubi.mightyarchitect.foundation.utility.AnimationTickHolder;
import com.simibubi.mightyarchitect.foundation.utility.outliner.Outliner;

import net.fabricmc.api.ClientModInitializer;

public class MightyClient implements ClientModInitializer {

	public static SchematicRenderer renderer = new SchematicRenderer();
	public static Outliner outliner = new Outliner();

	@Override
	public void onInitializeClient() {
		AllItems.initColorHandlers();
		Events.register();
		Keybinds.register();
		ArchitectManager.registerEvents();
	}

	public static void tick() {
		AnimationTickHolder.tick();
		ArchitectManager.tickBlockHighlightOutlines();
		MightyClient.outliner.tickOutlines();
		MightyClient.renderer.tick();
	}

	

}
