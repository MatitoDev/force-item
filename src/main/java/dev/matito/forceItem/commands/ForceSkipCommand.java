package dev.matito.forceItem.commands;

import de.mineking.databaseutils.Where;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.util.ItemEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceSkipCommand {
	public static void skip(CommandSender sender, Player player) {
		ItemEntry oldItem = ForceItem.INSTANCE.getItemTable().getCurrentItemEntry(player);
		ForceItem.INSTANCE.getSkippedItems().add(oldItem);
		ForceItem.INSTANCE.getItemTable().delete(Where.equals("player", player).and(Where.equals("itementry", oldItem.toString())));
		ForceItem.INSTANCE.getGameManager().nextItem(player);

		sender.sendMessage(ForceItem.getPrefix().append(Component.text("Skipped Item ", NamedTextColor.GREEN))
				.append(Component.text(oldItem.getName(), NamedTextColor.AQUA))
				.append(Component.text(" from player ", NamedTextColor.GREEN))
				.append(Component.text(player.getName(), NamedTextColor.AQUA)));

		player.sendMessage(ForceItem.getPrefix().append(Component.text("Your Item ", NamedTextColor.GREEN))
				.append(Component.text(oldItem.getName(), NamedTextColor.AQUA))
				.append(Component.text(" got skipped by ", NamedTextColor.GREEN))
				.append(Component.text(sender.getName(), NamedTextColor.AQUA)));
	}

	public static void register() {
		new CommandAPICommand("forceskip")
				.withPermission("force-item.forceskip")
				.withAliases("done")
				.withArguments(new PlayerArgument("player")
						.replaceSafeSuggestions(SafeSuggestions.suggest(info ->
								Bukkit.getOnlinePlayers().toArray(new Player[0])
						)))
				.executes((sender, args) -> {
					skip(sender, (Player) args.get("player"));
				})
				.register();
	}
}
