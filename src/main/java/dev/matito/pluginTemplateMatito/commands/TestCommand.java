package dev.matito.pluginTemplateMatito.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.matito.pluginTemplateMatito.PluginTemplateMatito;
import dev.matito.pluginTemplateMatito.database.table.TestTable;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TestCommand {
	public static void testCommand(CommandSender sender, String arg) {
		PluginTemplateMatito.INSTANCE.getTable().add((OfflinePlayer) sender, arg);
		sender.sendMessage(PluginTemplateMatito.getPrefix().append(Component.text("Test!")));
		sender.sendMessage(PluginTemplateMatito.getPrefix().append(Component.text("Value: ")
				.append(Component.text(PluginTemplateMatito.INSTANCE.getTable().get(arg).get().getValue().getName()))));
	}

	public static void register() {
		new CommandAPICommand("plugin-test")
				.withPermission("pluginTemplateMatito.test")
				.withArguments(new StringArgument("arg0"))
				.executes(((sender, args) -> {
					testCommand(sender, args.get(0).toString());
				}))
				.register(PluginTemplateMatito.INSTANCE);
	}

}
