package com.simibubi.mightyarchitect.foundation.utility.outliner;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.mightyarchitect.foundation.RenderTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class AABBOutline extends Outline {

	protected AxisAlignedBB bb;

	public AABBOutline(AxisAlignedBB bb) {
		this.setBounds(bb);
	}

	@Override
	public void render(MatrixStack ms, IRenderTypeBuffer buffer) {
		renderBB(ms, buffer, bb);
	}

	public void renderBB(MatrixStack ms, IRenderTypeBuffer buffer, AxisAlignedBB bb) {
		Vec3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo()
			.getProjectedView();
		boolean noCull = bb.contains(projectedView);
		bb = bb.grow(noCull ? -1 / 128d : 1 / 128d);
		noCull |= params.disableCull;

		Vec3d xyz = new Vec3d(bb.minX, bb.minY, bb.minZ);
		Vec3d Xyz = new Vec3d(bb.maxX, bb.minY, bb.minZ);
		Vec3d xYz = new Vec3d(bb.minX, bb.maxY, bb.minZ);
		Vec3d XYz = new Vec3d(bb.maxX, bb.maxY, bb.minZ);
		Vec3d xyZ = new Vec3d(bb.minX, bb.minY, bb.maxZ);
		Vec3d XyZ = new Vec3d(bb.maxX, bb.minY, bb.maxZ);
		Vec3d xYZ = new Vec3d(bb.minX, bb.maxY, bb.maxZ);
		Vec3d XYZ = new Vec3d(bb.maxX, bb.maxY, bb.maxZ);

		Vec3d start = xyz;
		renderAACuboidLine(ms, buffer, start, Xyz);
		renderAACuboidLine(ms, buffer, start, xYz);
		renderAACuboidLine(ms, buffer, start, xyZ);

		start = XyZ;
		renderAACuboidLine(ms, buffer, start, xyZ);
		renderAACuboidLine(ms, buffer, start, XYZ);
		renderAACuboidLine(ms, buffer, start, Xyz);

		start = XYz;
		renderAACuboidLine(ms, buffer, start, xYz);
		renderAACuboidLine(ms, buffer, start, Xyz);
		renderAACuboidLine(ms, buffer, start, XYZ);

		start = xYZ;
		renderAACuboidLine(ms, buffer, start, XYZ);
		renderAACuboidLine(ms, buffer, start, xyZ);
		renderAACuboidLine(ms, buffer, start, xYz);

		renderFace(ms, buffer, Direction.NORTH, xYz, XYz, Xyz, xyz, noCull);
		renderFace(ms, buffer, Direction.SOUTH, XYZ, xYZ, xyZ, XyZ, noCull);
		renderFace(ms, buffer, Direction.EAST, XYz, XYZ, XyZ, Xyz, noCull);
		renderFace(ms, buffer, Direction.WEST, xYZ, xYz, xyz, xyZ, noCull);
		renderFace(ms, buffer, Direction.UP, xYZ, XYZ, XYz, xYz, noCull);
		renderFace(ms, buffer, Direction.DOWN, xyz, Xyz, XyZ, xyZ, noCull);

	}

	protected void renderFace(MatrixStack ms, IRenderTypeBuffer buffer, Direction direction, Vec3d p1, Vec3d p2,
		Vec3d p3, Vec3d p4, boolean noCull) {
		if (!params.faceTexture.isPresent())
			return;
		if (params.isFaceHidden(direction))
			return;

		ResourceLocation faceTexture = params.faceTexture.get()
			.getLocation();
		float alphaBefore = params.alpha;
		params.alpha =
			(direction == params.getHighlightedFace() && params.hightlightedFaceTexture.isPresent()) ? 1 : alphaBefore;

		RenderType translucentType = RenderTypes.getOutlineTranslucent(faceTexture, !noCull);
		IVertexBuilder builder = buffer.getBuffer(translucentType);

		Axis axis = direction.getAxis();
		Vec3d uDiff = p2.subtract(p1);
		Vec3d vDiff = p4.subtract(p1);
		float maxU = (float) Math.abs(axis == Axis.X ? uDiff.z : uDiff.x);
		float maxV = (float) Math.abs(axis == Axis.Y ? vDiff.z : vDiff.y);
		putQuadUVColor(ms, builder, p1, p2, p3, p4, params.faceRgb == null ? params.rgb : params.faceRgb, 0, 0, maxU,
			maxV, Direction.UP);
		params.alpha = alphaBefore;
	}

	public void setBounds(AxisAlignedBB bb) {
		this.bb = bb;
	}

}
