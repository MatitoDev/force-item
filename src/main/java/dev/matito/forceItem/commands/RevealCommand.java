package dev.matito.forceItem.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.matito.forceItem.ForceItem;
import dev.matito.forceItem.database.object.Item;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.ScrollingGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RevealCommand {

	static Gui placementGui, placementSenderGui;
	static HashMap<Player, ScrollingGui> revealedGui = new HashMap<>();

	public static void reveal(CommandSender sender) {

		List<Player> playersFinishedItems = ForceItem.INSTANCE.getItemTable().getPlayersFinishedItems().keySet().stream().toList();
		for (int i = 1; i < playersFinishedItems.size() + 1; i++) {
			placementGui.addItem(createUnknownPlayer(i, playersFinishedItems.get(i - 1), false, (Player) sender));
			placementSenderGui.addItem(createUnknownPlayer(i, playersFinishedItems.get(i - 1),true, (Player) sender));
		}

		ForceItem.INSTANCE.getServer().getOnlinePlayers().forEach(p ->
				p.sendMessage(ForceItem.getPrefix()
						.append(Component.text("Click Here to open Reveal" , NamedTextColor.AQUA, TextDecoration.BOLD))
							.clickEvent(ClickEvent.callback(event -> placementGui.open(p))))
		);


		openPlacementGui((Player) sender);
	}

	public static void register() {
		new CommandAPICommand("reveal")
				.withPermission("force-item.reveal")
				.executesPlayer(((sender, args) -> {
					reveal(sender);
				}))
				.register();

		placementGui = Gui.gui()
				.disableAllInteractions()
				.title(Component.text("Placement"))
				.type(GuiType.HOPPER)
				.create();

		placementSenderGui = Gui.gui()
				.disableAllInteractions()
				.title(Component.text("Placement"))
				.type(GuiType.HOPPER)
				.create();

	}

	private static GuiItem createUnknownPlayer(int place, Player player, boolean isSenderGuiItem, Player pSender) {
		return PaperItemBuilder
				.from(Material.SKELETON_SKULL)
				.name(Component.text(place + ". Place Player", NamedTextColor.GRAY))
				.lore(List.of(Component.text( isSenderGuiItem ? "Click to reveal items" : "", NamedTextColor.DARK_GRAY)))
				.setNbt("revealed", false)
				.setNbt("player", player.getUniqueId().toString())
				.asGuiItem(event -> {
					if (!isSenderGuiItem) return;
					int[] id = new int[1];

					placementGui.setItem(place - 1, createKnownPlayer(place, player));
					placementSenderGui.setItem(place - 1, createKnownPlayer(place, player));

					ScrollingGui gui = Gui.scrolling(ScrollType.VERTICAL)
							.rows(6)
							.pageSize(36)
							.disableAllInteractions()
							.title(Component.text("Items from ").append(Component.text("Player :)", NamedTextColor.AQUA, TextDecoration.BOLD, TextDecoration.OBFUSCATED)))
							.create();

					revealedGui.put(player, gui);

					for (int i = 0; i <= 8; i++) {
						if (i == 0) {
							gui.setItem(i, PaperItemBuilder.from(Material.RED_CONCRETE)
									.name(Component.text("Back", NamedTextColor.RED))
									.asGuiItem(inventoryClickEvent -> {
										if (event.getWhoClicked() == pSender) {
											openPlacementGui(pSender);
										} else {
											placementGui.open(event.getWhoClicked());
										}
									}));
							continue;
						}

						if (i == 4) {
							gui.setItem(i, PaperItemBuilder.from(Material.SKELETON_SKULL)
									.name(Component.text("Player", NamedTextColor.GRAY))
									.asGuiItem());
							continue;
						}
						gui.setItem(i, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
								.name(Component.text("", NamedTextColor.GRAY))
								.asGuiItem());
					}

					for (int i = 45; i <= 53 ; i++) {
						gui.setItem(i, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
								.name(Component.text("", NamedTextColor.GRAY))
								.asGuiItem());
					}

					ForceItem.INSTANCE.getServer().getOnlinePlayers().forEach(gui::open);
					List<Item> sortedItems = ForceItem.INSTANCE.getItemTable().getFinishedItems(player);
					sortedItems.sort(Comparator.comparingDouble(Item::getTimeInSeconds).reversed());

					AtomicInteger slot = new AtomicInteger(9);
					AtomicInteger page = new AtomicInteger(0);

					id[0] = ForceItem.INSTANCE.getServer().getScheduler().scheduleSyncRepeatingTask(ForceItem.INSTANCE, () -> {
						int index = slot.get() - (9 - page.get() * 9);
						if (index >= sortedItems.size()) {
							ForceItem.INSTANCE.getServer().getScheduler().cancelTask(id[0]);
							revealPlayer(player, gui);
							return;
						}


						gui.addItem(PaperItemBuilder.from(sortedItems.get(index).getItemEntry().getItemStack())
								.lore(List.of(Component.text("Time: " + sortedItems.get(index).getTime(), NamedTextColor.GREEN),
										sortedItems.get(index).isSkipped() ? Component.text("JOKER", NamedTextColor.GOLD) : Component.empty()))
								.asGuiItem()
						);


						if (slot.get() >= 45) {
							gui.next();
							slot.set(36);
							page.getAndIncrement();
							gui.setItem(51, PaperItemBuilder.from(Material.PAPER).name(Component.text("Scroll down", NamedTextColor.RED)).asGuiItem(inventoryClickEvent -> gui.next()));
							gui.setItem(47, PaperItemBuilder.from(Material.PAPER).name(Component.text("Scroll up", NamedTextColor.GREEN)).asGuiItem(inventoryClickEvent -> gui.previous()));
						}
						gui.update();

						ForceItem.INSTANCE.getServer().getOnlinePlayers().forEach(p -> p.playSound(player, Sound.ENTITY_ITEM_PICKUP, 255, 0.2f));
						slot.getAndIncrement();


					}, 20, 15);
				});
	}

	static private GuiItem createKnownPlayer(int place, Player player) {
		return PaperItemBuilder
				.skull()
				.owner(player)
				.name(Component.text(place + ". Place: ", NamedTextColor.GREEN)
						.append(Component.text(player.getName(), NamedTextColor.AQUA))
						.append(Component.text(" (", NamedTextColor.DARK_GRAY))
						.append(Component.text(ForceItem.INSTANCE.getItemTable().getFinishedItems(player).size(), NamedTextColor.GRAY))
						.append(Component.text(")", NamedTextColor.DARK_GRAY))
				)
				.lore(List.of(Component.text( "Click to show items" , NamedTextColor.DARK_GRAY)))
				.setNbt("revealed", true)
				.setNbt("player", player.getUniqueId().toString())
				.asGuiItem(event -> revealedGui.get(player).open(event.getWhoClicked()));
	}

	static private void revealPlayer(Player player, ScrollingGui gui) {
		gui.updateTitle(Component.text("Items from ")
				.append(Component.text(player.getName(), NamedTextColor.AQUA, TextDecoration.BOLD))
				.append(Component.text(" (", NamedTextColor.DARK_GRAY))
				.append(Component.text(ForceItem.INSTANCE.getItemTable().getFinishedItems(player).size(), NamedTextColor.DARK_GRAY))
				.append(Component.text(")", NamedTextColor.DARK_GRAY))
		);
		gui.updateItem(4, PaperItemBuilder.skull().owner(player).name(Component.text(player.getName(), NamedTextColor.AQUA)).asGuiItem());
		ForceItem.INSTANCE.getServer().getOnlinePlayers().forEach(p -> p.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 255, 0.4f));
	}

	static private void openPlacementGui(Player pSender) {
		ForceItem.INSTANCE.getServer().getOnlinePlayers().forEach(placementGui::open);
		pSender.closeInventory();
		placementSenderGui.open(pSender);
	}
}
