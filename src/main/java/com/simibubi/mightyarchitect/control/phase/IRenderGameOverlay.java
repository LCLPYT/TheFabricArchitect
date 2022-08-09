package com.simibubi.mightyarchitect.control.phase;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IRenderGameOverlay {

	void renderGameOverlay(PoseStack stack, float partialTicks);
	
}
