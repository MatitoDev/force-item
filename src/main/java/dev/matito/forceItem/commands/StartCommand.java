package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.matito.forceItem.ForceItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class StartCommand {
	public static void start(CommandSender sender) {
		if (ForceItem.INSTANCE.getGameManager().isRunning()) {
			sender.sendMessage(ForceItem.getPrefix().append(Component.text("Game already running!", NamedTextColor.RED)));
			return;
		}
		if (ForceItem.INSTANCE.getItemTable().getPlayersCurrentItems().isEmpty()) {
			sender.sendMessage(ForceItem.getPrefix().append(Component.text("No Players added yet!", NamedTextColor.RED)));
			return;
		}
		ForceItem.INSTANCE.getGameManager().start();
	}

	public static void register() {
		new CommandAPICommand("start")
				.withPermission("force-item.start")
				.executes(((sender, args) -> {
					start(sender);
				}))
				.register(ForceItem.INSTANCE);
	}
}
