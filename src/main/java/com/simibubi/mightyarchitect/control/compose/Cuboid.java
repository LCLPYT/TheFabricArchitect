package com.simibubi.mightyarchitect.control.compose;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class Cuboid {

	public int x;
	public int y;
	public int z;
	public int width;
	public int height;
	public int length;

	public Cuboid(BlockPos origin, BlockPos size) {
		this(origin, size.getX(), size.getY(), size.getZ());
	}

	public Cuboid(BlockPos origin, int width, int height, int length) {
		this.x = origin.getX() + Math.min(width, 0);
		this.y = origin.getY() + Math.min(height, 0);
		this.z = origin.getZ() + Math.min(length, 0);
		this.width = Math.abs(width);
		this.height = Math.abs(height);
		this.length = Math.abs(length);
	}

	public BlockPos getOrigin() {
		return new BlockPos(x, y, z);
	}

	public BlockPos getSize() {
		return new BlockPos(width, height, length);
	}

	@Override
	public Cuboid clone() {
		return new Cuboid(new BlockPos(x, y, z), width, height, length);
	}

	public void move(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	public void centerHorizontallyOn(BlockPos pos) {
		x = pos.getX() - (width / 2);
		y = pos.getY();
		z = pos.getZ() - (length / 2);
	}

	public boolean intersects(Cuboid other) {
		return !(other.x >= x + width || other.z >= z + length || other.x + other.width <= x
				|| other.z + other.length <= z);
	}

	public boolean contains(BlockPos pos) {
		return (pos.getX() >= x && pos.getX() < x + width) && (pos.getY() >= y && pos.getY() < y + height)
				&& (pos.getZ() >= z && pos.getZ() < z + length);
	}

	public BlockPos getCenter() {
		return getOrigin().offset(width / 2, height / 2, length / 2);
	}

	public void moveToAttach(Room other, Direction side, int shift) {
		if (side != Direction.EAST && side != Direction.WEST)
			centerOnOthersX(other, shift);

		if (side != Direction.NORTH && side != Direction.SOUTH)
			centerOnOthersZ(other, shift);

		switch (side) {
			case WEST -> this.x = other.x + other.width;
			case EAST -> this.x = other.x - this.width;
			case SOUTH -> this.z = other.z + other.length;
			case NORTH -> this.z = other.z - this.length;
			case UP -> this.y = other.y + other.height;
			case DOWN -> this.y = other.y - this.height;
			default -> {
			}
		}
	}

	private void centerOnOthersZ(Cuboid other, int shift) {
		this.z = other.z + shift + (other.length - this.length) / 2;
	}

	private void centerOnOthersX(Cuboid other, int shift) {
		this.x = other.x + shift + (other.width - this.width) / 2;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Cuboid && ((Cuboid) obj).getOrigin().equals(getOrigin())
				&& ((Cuboid) obj).getSize().equals(getSize());
	}
	
	public BoundingBox toMBB() {
		return BoundingBox.fromCorners(getOrigin(), getOrigin().offset(getSize()));
	}
	
	public AABB toAABB() {
		return new AABB(getOrigin(), getOrigin().offset(getSize()));
	}

}