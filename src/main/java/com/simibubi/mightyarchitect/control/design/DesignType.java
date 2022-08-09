package com.simibubi.mightyarchitect.control.design;

import com.google.common.collect.ImmutableList;
import com.simibubi.mightyarchitect.control.design.partials.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum DesignType {

	WALL("wall", "Wall", new Wall()), 
	FACADE("facade", "Facade", new Facade()), 
	CORNER("corner", "Corner", new Corner()), 
	TOWER("tower", "Tower", new Tower()), 
	TRIM("trim", "Trim", new Trim()), 
	ROOF("roof", "Gable Roof", new Roof()), 
	FLAT_ROOF("flatroof", "Flat Roof", new FlatRoof()), 
	TOWER_ROOF("towerroof", "Conical Roof", new TowerRoof()), 
	TOWER_FLAT_ROOF("towerflatroof", "Flat Tower Roof", new TowerFlatRoof()), 
	
	NONE("none", "None", null);

	private final String filePath;
	private final String displayName;
	private final Design design;

	DesignType(String filePath, String displayName, Design design) {
		this.filePath = filePath;
		this.displayName = displayName;
		this.design = design;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Design getDesign() {
		return design;
	}

	public boolean hasAdditionalData() {
		return hasSizeData() || hasSubtypes();
	}

	public String getAdditionalDataName() {
		return switch (this) {
			case ROOF -> "Roof Span";
			case FLAT_ROOF -> "Margin";
			case TOWER, TOWER_FLAT_ROOF, TOWER_ROOF -> "Tower Radius";
			case WALL -> "Size Behaviour";
			default -> "";
		};
	}

	public boolean hasSizeData() {
		return switch (this) {
			case FLAT_ROOF, ROOF, TOWER, TOWER_FLAT_ROOF, TOWER_ROOF -> true;
			default -> false;
		};
	}
	
	public int getMaxSize() {
		return switch (this) {
			case ROOF -> ThemeStatistics.MAX_ROOF_SPAN;
			case FLAT_ROOF -> ThemeStatistics.MAX_MARGIN;
			case TOWER, TOWER_FLAT_ROOF, TOWER_ROOF -> ThemeStatistics.MAX_TOWER_RADIUS;
			default -> 0;
		};
	}
	
	public int getMinSize() {
		return switch (this) {
			case ROOF -> ThemeStatistics.MIN_ROOF_SPAN;
			case FLAT_ROOF -> ThemeStatistics.MIN_MARGIN;
			case TOWER, TOWER_FLAT_ROOF, TOWER_ROOF -> ThemeStatistics.MIN_TOWER_RADIUS;
			default -> 0;
		};
	}

	public boolean hasSubtypes() {
		return this == DesignType.WALL;
	}

	public List<String> getSubtypeOptions() {
		if (this == DesignType.WALL) {
			List<String> list = new ArrayList<>();
			ImmutableList.copyOf(Wall.ExpandBehaviour.values()).forEach(value -> list.add(value.name()));
			return list;
		}
		return Collections.emptyList();
	}
	
	public static List<DesignType> defaults() {
		return ImmutableList.of(WALL, FACADE, CORNER);
	}
	
	public static List<DesignType> roofTypes() {
		return ImmutableList.of(ROOF, FLAT_ROOF, TOWER_FLAT_ROOF, TOWER_ROOF);
	}

}
