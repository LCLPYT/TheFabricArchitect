package com.simibubi.mightyarchitect.control.design;

import com.simibubi.mightyarchitect.control.compose.Room;
import com.simibubi.mightyarchitect.control.compose.Stack;
import com.simibubi.mightyarchitect.control.design.partials.Design;

import java.util.HashMap;
import java.util.Map;

public class RoomDesignCache {
	
	private final Map<Room, Design> cachedDesigns;
	
	public RoomDesignCache() {
		cachedDesigns = new HashMap<>();
	}

	public void rerollAll() {
		cachedDesigns.clear();
	}
	
	public void rerollRoom(Room room) {
		cachedDesigns.remove(room);
	}
	
	public void rerollStack(Stack stack) {
		stack.forEach(this::rerollRoom);
	}

}
