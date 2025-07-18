package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.matito.forceItem.ForceItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

public class ResetCommand {
	public static void reset(CommandSender sender) {
		ForceItem.INSTANCE.getTimer().stopTimer();
		ForceItem.INSTANCE.getItemTable().deleteAll();
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Rested Database", NamedTextColor.RED)));
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("You now have to add all Players again with ", NamedTextColor.GREEN))
				.append(Component.text("/addplayer", NamedTextColor.AQUA, TextDecoration.UNDERLINED).clickEvent(ClickEvent.suggestCommand("/addplayer "))));
	}

	public static void register() {
		new CommandAPICommand("reset")
				.withPermission("force-item.reset")
				.executes(((sender, args) -> {
					reset(sender);
				}))
				.register();
	}
}
