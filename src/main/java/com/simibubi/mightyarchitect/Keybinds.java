package com.simibubi.mightyarchitect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.simibubi.mightyarchitect.mixin.KeyMappingAccessor;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public enum Keybinds {

	ACTIVATE("activate", Type.KEYSYM, GLFW.GLFW_KEY_G),
	FOCUL_TOOL_MENU("focus_tool_menu", Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT),

	;

	public static interface KeybindListener {
		void keybindTriggered(boolean pressed);
	}

	private KeyMapping keybind;
	private String description;
	private int key;
	private boolean modifiable;
	private List<KeybindListener> callbacks;
	private Type initialType;

	private Keybinds(int fixedKey, Type initialType) {
		this("", initialType, fixedKey);
	}

	private Keybinds(String description, Type initialType, int defaultKey) {
		this.initialType = initialType;
		this.description = TheMightyArchitect.ID + ".keyinfo." + description;
		this.key = defaultKey;
		this.modifiable = !description.isEmpty();
		this.callbacks = new ArrayList<>();
	}

	public void notifyMe(KeybindListener callback) {
		callbacks.add(callback);
	}

	public static void register() {
		for (Keybinds key : values()) {
			key.keybind = new KeyMapping(key.description, key.initialType, key.key, TheMightyArchitect.NAME);
			if (!key.modifiable)
				continue;
			KeyBindingHelper.registerKeyBinding(key.keybind);
		}
	}

	public static void handleKey(int key, int scanCode, int action, int modifiers) {
		handleInput(action, k -> k.getKeybind()
				.matches(key, scanCode));
	}

	public static void handleMouseButton(int button, int action, int modifiers) {
		handleInput(action, k -> k.getKeybind()
				.matchesMouse(action));
	}

	private static void handleInput(int action, Predicate<Keybinds> filter) {
		if (action == InputConstants.REPEAT)
			return;
		Arrays.stream(values())
			.filter(filter)
			.flatMap(k -> k.callbacks.stream())
			.forEach(kl -> kl.keybindTriggered(action == InputConstants.PRESS));
	}

	public KeyMapping getKeybind() {
		return keybind;
	}

	public boolean isPressed() {
		if (!modifiable)
			return isKeyDown(key);
		return keybind.isDown();
	}

	public String getBoundKey() {
		return keybind.getTranslatedKeyMessage()
			.getString()
			.toUpperCase();
	}

	public boolean matches(int key) {
		return ((KeyMappingAccessor) keybind).getKey()
			.getValue() == key;
	}

	public int getBoundCode() {
		return ((KeyMappingAccessor) keybind).getKey()
			.getValue();
	}

	public static boolean isKeyDown(int key) {
		return InputConstants.isKeyDown(Minecraft.getInstance()
			.getWindow()
			.getWindow(), key);
	}

	public static boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(Minecraft.getInstance()
			.getWindow()
			.getWindow(), button) == 1;
	}

	public static boolean ctrlDown() {
		return Screen.hasControlDown();
	}

	public static boolean shiftDown() {
		return Screen.hasShiftDown();
	}

	public static boolean altDown() {
		return Screen.hasAltDown();
	}

}
