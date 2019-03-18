package com.simibubi.mightyarchitect.command;

import com.simibubi.mightyarchitect.control.ArchitectManager;
import com.simibubi.mightyarchitect.gui.GuiOpener;
import com.simibubi.mightyarchitect.gui.GuiTextPrompt;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

public class CommandPalette extends CommandBase implements IClientCommand {

	@Override
	public String getName() {
		return "palette";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/palette create | save <name>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayer) {
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "create":
					ArchitectManager.createPalette(true);
					break;
				case "save":
					if (args.length > 1) {
						String name = "";
						for (int i = 1; i < args.length; i++) {
							name += args[i] + ((i == args.length - 1) ? "" : " ");
						}
						ArchitectManager.finishPalette(name);
					} else {
						GuiTextPrompt gui = new GuiTextPrompt(result -> ArchitectManager.finishPalette(result),
								result -> ArchitectManager.pickPalette());
						gui.setButtonTextConfirm("Save and Apply");
						gui.setButtonTextAbort("Cancel");
						gui.setTitle("Enter a name for your Palette:");
						GuiOpener.open(gui);
					}
					break;
				default:
					throw new CommandException("Subcommands for /palette: create, save");
				}
			} else {
				ArchitectManager.pickPalette();
			}
		}

	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return false;
	}

}