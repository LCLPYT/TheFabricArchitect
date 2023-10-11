package com.simibubi.mightyarchitect.control.phase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.mightyarchitect.MightyClient;
import com.simibubi.mightyarchitect.control.Schematic;
import com.simibubi.mightyarchitect.control.palette.Palette;
import com.simibubi.mightyarchitect.control.palette.PaletteDefinition;
import com.simibubi.mightyarchitect.foundation.utility.Lang;

import com.simibubi.mightyarchitect.render.RenderTypeHelper;
import com.simibubi.mightyarchitect.util.EnvHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PhaseCreatingPalette extends PhaseBase implements IDrawBlockHighlights {

	private PaletteDefinition palette;
	private BlockPos center;
	private Map<BlockPos, Palette> grid;
	private boolean[] changed;

	@Override
	public void whenEntered() {

		Schematic model = getModel();
		ClientLevel world = minecraft.level;
		changed = new boolean[16];

		palette = model.getCreatedPalette();
		center = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, minecraft.player.blockPosition());
		grid = new HashMap<>();

		for (int i = 0; i < 16; i++) {
			BlockPos pos = positionFromIndex(i);
			grid.put(pos, Palette.values()[i]);
			if (!world.isEmptyBlock(pos) && palette.get(Palette.values()[i]) != world.getBlockState(pos)) {
				palette.put(Palette.values()[i], world.getBlockState(pos));
				changed[i] = true;
			}
		}

		model.updatePalettePreview();
		MightyClient.renderer.display(getModel());
	}

	@Override
	public void update() {
		for (int i = 0; i < 16; i++) {
			BlockPos pos = positionFromIndex(i);

			// Handle changes
			if (minecraft.level.isEmptyBlock(pos)) {
				PaletteDefinition paletteDef =
					getModel().isEditingPrimary() ? getModel().getPrimary() : getModel().getSecondary();
				Palette key = grid.get(pos);

				if (paletteDef.get(key) != palette.get(key)) {
					palette.put(key, paletteDef.get(key));
					changed[i] = false;
					notifyChange();
				}

				continue;
			}

			BlockState state = minecraft.level.getBlockState(pos);
			if (state.getBlock() instanceof TrapDoorBlock)
				state = state.setValue(TrapDoorBlock.OPEN, true);

			if (palette.get(Palette.values()[i]) != state) {
				palette.put(grid.get(pos), state);
				changed[i] = true;
				notifyChange();
			}
		}
	}

	@Override
	public void render(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
		// Blocks
		for (int i = 0; i < 16; i++) {
			BlockState state = palette.get(Palette.values()[i]);

			if (state == null)
				continue;
			if (changed[i])
				continue;
			BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
			BakedModel blockModel = blockRenderer.getBlockModel(state);
			if (blockModel == null)
				continue;

			ms.pushPose();
			BlockPos translate = positionFromIndex(i);
			ms.translate(translate.getX() - camera.x, translate.getY() - camera.y, translate.getZ() - camera.z);
			ms.scale(15 / 16f, 15 / 16f, 15 / 16f);
			ms.translate(1 / 32f, 1 / 32f, 1 / 32f);

			for (RenderType renderType : RenderTypeHelper.getRenderTypes(state))
				minecraft.getBlockRenderer()
					.renderBatched(state, translate, minecraft.level, ms, buffer.getBuffer(renderType), false,
						minecraft.level.random);

			ms.popPose();
		}
	}

	@Override
	public void whenExited() {
		getModel().stopPalettePreview();
		MightyClient.renderer.setActive(false);
	}

	protected void notifyChange() {
		getModel().updatePalettePreview();
		Lang.text("Updating Preview...")
			.sendStatus(minecraft.player);
		MightyClient.renderer.update();
	}

	static final Object textKey = new Object();

	@Override
	public void tickHighlightOutlines() {
		Vec3 from = minecraft.player.getEyePosition();
		Vec3 to = from.add(minecraft.player.getLookAngle()
			.normalize()
			.scale(10));

		for (int i = 0; i < 16; i++) {
			BlockPos pos = positionFromIndex(i);
			AABB bb = new AABB(pos);

			// Render Outline
			boolean b = changed[i];
			boolean s = bb.clip(from, to)
				.isPresent();

			if (s)
				sendStatusMessage(grid.get(pos)
					.getDisplayName());

			MightyClient.outliner.showAABB("pallete" + i, bb)
				.lineWidth(b || s ? 1 / 16f : 1 / 24f)
				.colored(s ? 0x6677ee : b ? 0xccccdd : 0x666677);
		}

	}

	private BlockPos positionFromIndex(int index) {
		return center.east(-3 + (index % 4) * 2)
			.south(-3 + (index / 4) * 2);
	}

	@Override
	public List<String> getToolTip() {
		return ImmutableList.of("The Ghost blocks show the individual materials used in this build.",
			"Modify the palette by placing blocks into the marked areas. You do not have to fill all the gaps.",
			"Once finished, make sure to save it. [F]");
	}

}
