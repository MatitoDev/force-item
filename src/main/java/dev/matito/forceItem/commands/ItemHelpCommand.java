package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.util.ItemEntry;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemHelpCommand {
	public static void itemHelp(Player sender) {
		ItemEntry item = ForceItem.INSTANCE.getItemTable().getCurrentItemEntry(sender);

		if (item == null) {
			sender.sendMessage(ForceItem.getPrefix().append(Component.text("No Item found!", NamedTextColor.RED)));
			return;
		}
		Gui gui = Gui.gui()
				.title(Component.text("Your Item: ").append(Component.text(item.getName(), NamedTextColor.AQUA)))
				.type(GuiType.HOPPER)
				.disableAllInteractions()
				.create();

		gui.setItem(2, PaperItemBuilder.from(item.getItemStack()).asGuiItem());
		gui.open(sender);
	}

	public static void register() {
		new CommandAPICommand("itemhelp")
				.withAliases("item", "i")
				.executesPlayer((sender, args) -> {
					itemHelp(sender);
				})
				.register();
	}
}
