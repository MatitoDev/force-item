package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.listener.JokerListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveJokerCommand {
	public static void giveJoker(CommandSender sender, Player player, int count) {
		player.give(JokerListener.getJokerItem(count));
		player.updateInventory();
		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Gave ", NamedTextColor.GREEN))
				.append(Component.text(player.getName(), NamedTextColor.AQUA))
				.append(Component.text(" joker ", NamedTextColor.GREEN))
				.append(Component.text(count + "x", NamedTextColor.AQUA))
		);
	}

	public static void register() {
		new CommandAPICommand("givejoker")
				.withPermission("force-item.givejoker")
				.withArguments(new PlayerArgument("player")
						.replaceSafeSuggestions(SafeSuggestions.suggest(info -> Bukkit.getOnlinePlayers().toArray(new Player[0]))),
						new IntegerArgument("count", 1, 64))
				.executes(((sender, args) -> {
					giveJoker(sender, (Player) args.get("player"), (Integer) args.get("count"));
				}))
				.register();
	}
}
