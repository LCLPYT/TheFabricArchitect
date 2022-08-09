package com.simibubi.mightyarchitect.control.design;

import com.simibubi.mightyarchitect.control.compose.CylinderStack;
import com.simibubi.mightyarchitect.control.compose.GroundPlan;
import com.simibubi.mightyarchitect.control.compose.Room;
import com.simibubi.mightyarchitect.control.compose.Stack;
import com.simibubi.mightyarchitect.control.design.partials.Design;
import com.simibubi.mightyarchitect.control.design.partials.Design.DesignInstance;
import com.simibubi.mightyarchitect.foundation.utility.DesignHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesignPicker {

	private DesignTheme theme;
	private final Map<Room, RoomDesignMapping> roomDesigns;
	private final Map<Stack, Design> roofDesigns;

	public DesignPicker() {
		roomDesigns = new HashMap<>();
		roofDesigns = new HashMap<>();
	}

	public void reset() {
		rerollAll();
	}

	public Sketch assembleSketch(GroundPlan groundPlan, int seed) {
		return pickDesigns(groundPlan, seed);
	}

	private Sketch pickDesigns(GroundPlan groundPlan, int seed) {
		Sketch sketch = new Sketch();
		TemporaryDesignCache provider = new TemporaryDesignCache(roomDesigns, roofDesigns, seed);

		groundPlan.forEachStack(stack -> stack.forEach(room -> {

			List<DesignInstance> designList = room.secondaryPalette ? sketch.secondary : sketch.primary;

			if (stack instanceof CylinderStack) {
				DesignHelper.addTower(provider, designList, theme, room.designLayer, room);

			} else {
				DesignHelper.addCuboid(provider, designList, theme, room.designLayer, room);
			}

			if (room != stack.highest())
				return;

			DesignLayer roofLayer = DesignLayer.Roofing;

			switch (room.roofType) {
				case ROOF -> {
					if (stack instanceof CylinderStack) {
						DesignHelper.addTowerRoof(provider, designList, theme, roofLayer, stack, false);
						break;
					}
					if (room.quadFacadeRoof) {
						DesignHelper.addNormalCrossRoof(provider, designList, theme, roofLayer, stack);
						break;
					}
					DesignHelper.addNormalRoof(provider, designList, theme, roofLayer, stack);
				}
				case FLAT_ROOF -> {
					if (stack instanceof CylinderStack) {
						DesignHelper.addTowerRoof(provider, designList, theme, roofLayer, stack, true);
						break;
					}
					DesignHelper.addFlatRoof(provider, designList, theme, roofLayer, stack);
				}
				default -> {
				}
			}

		}));

		sketch.interior = groundPlan.getInterior();
		return sketch;
	}

	public RoomDesignMapping getCachedRoom(Room room) {
		if (roomDesigns.containsKey(room))
			return roomDesigns.get(room);
		return null;
	}

	public Design getCachedRoof(Stack stack) {
		if (roofDesigns.containsKey(stack))
			return roofDesigns.get(stack);
		return null;
	}

	public void rerollAll() {
		roomDesigns.clear();
		roofDesigns.clear();
	}

	public void rerollRoom(Room room) {
		roomDesigns.remove(room);
	}

	public void rerollRoof(Stack stack) {
		roofDesigns.remove(stack);
	}

	public void rerollStack(Stack stack) {
		stack.forEach(this::rerollRoom);
		rerollRoof(stack);
	}

	public void putRoom(Room room, RoomDesignMapping mapping) {
		roomDesigns.put(room, mapping);
	}

	public void putRoof(Stack stack, Design roof) {
		roofDesigns.put(stack, roof);
	}

	public void setTheme(DesignTheme theme) {
		this.theme = theme;
	}

	public static class RoomDesignMapping {
		public Design wall1;
		public Design wall2;
		public Design corner;

		public RoomDesignMapping(Design tower) {
			wall1 = tower;
		}

		public RoomDesignMapping(Design wall1, Design wall2, Design corner) {
			this.wall1 = wall1;
			this.wall2 = wall2;
			this.corner = corner;
		}
	}

}
