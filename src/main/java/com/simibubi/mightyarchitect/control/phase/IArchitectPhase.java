package com.simibubi.mightyarchitect.control.phase;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.List;

public interface IArchitectPhase {

	void whenEntered();
	void update();
	void render(PoseStack ms, MultiBufferSource buffer);
	void whenExited();
	
	List<String> getToolTip();
	
	void onClick(int button);
	void onKey(int key, boolean released);
	boolean onScroll(int amount);
	
}
