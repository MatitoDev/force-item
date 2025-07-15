package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.GameManager;
import org.bukkit.command.CommandSender;

public class StartCommand {
	public static void start(CommandSender sender) {
		ForceItem.INSTANCE.getGameManager().start();
	}

	public static void register() {
		new CommandAPICommand("start")
				.withPermission("force-item.start")
				//.withArguments(new StringArgument("arg0"))
				.executes(((sender, args) -> {
					start(sender);
				}))
				.register(ForceItem.INSTANCE);
	}
}
