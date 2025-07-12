package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.matito.forceItem.ForceItem;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class TestCommand {
	public static void testCommand(CommandSender sender, String arg) {
		ForceItem.INSTANCE.getTable().add((OfflinePlayer) sender, arg);
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Test!")));
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Value: ")
				.append(Component.text(ForceItem.INSTANCE.getTable().get(arg).get().getValue().getName()))));
	}

	public static void register() {
		new CommandAPICommand("plugin-test")
				.withPermission("pluginTemplateMatito.test")
				.withArguments(new StringArgument("arg0"))
				.executes(((sender, args) -> {
					testCommand(sender, args.get(0).toString());
				}))
				.register(ForceItem.INSTANCE);
	}

}
