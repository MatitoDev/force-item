package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.GameManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddPlayerCommand {
	public static void addPlayer(CommandSender sender, Player player) {
		ForceItem.INSTANCE.getItemTable().add(player, GameManager.getNewItem(player));
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Added ", NamedTextColor.GREEN))
				.append(Component.text(player.getName(), NamedTextColor.AQUA)));
	}

	public static void register() {
		new CommandAPICommand("addplayer")
				.withPermission("force-item.addplayer")
				.withArguments(new PlayerArgument("player")
						.replaceSafeSuggestions(SafeSuggestions.suggest(info ->
								Bukkit.getOnlinePlayers().toArray(new Player[0])
						)))
				.executes(((sender, args) -> {
					addPlayer(sender, (Player) args.get("player"));
				}))
				.register(ForceItem.INSTANCE);
	}
}
