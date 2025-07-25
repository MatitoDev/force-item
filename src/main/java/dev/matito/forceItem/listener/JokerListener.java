package dev.matito.forceItem.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.matito.forceItem.ForceItem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class JokerListener implements Listener {
	Map<Player, Integer> deadJokers = new HashMap<>();

	public static ItemStack getJokerItem(int count) {
		ItemStack stack = new ItemStack(Material.BARRIER, count);
		ItemMeta meta = stack.getItemMeta();
		meta.displayName(Component.text("Joker", NamedTextColor.GOLD, TextDecoration.BOLD));
		meta.lore(List.of(Component.text("Click to skip the current Item", NamedTextColor.GRAY)));
		stack.setItemMeta(meta);
		return stack;
	}

	@EventHandler()
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getMaterial() != Material.BARRIER || !ForceItem.INSTANCE.getTimer().getTimerStatus()) return;

		handleJoker(event.getPlayer());
		event.setCancelled(true);

		if (event.getItem().getAmount() > 1) event.getItem().setAmount(event.getItem().getAmount() - 1);
		else event.getPlayer().getInventory().setItem(event.getHand(), null);

		event.getPlayer().updateInventory();
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		int jokerCount = event.getDrops().stream()
				.filter(Objects::nonNull)
				.filter(item -> item.getType() == Material.BARRIER)
				.mapToInt(ItemStack::getAmount)
				.sum();
		event.getDrops().removeIf(item -> item != null && item.getType() == Material.BARRIER);
		if (jokerCount > 0) {
			deadJokers.put(event.getEntity(), jokerCount);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
		event.getPlayer().give(JokerListener.getJokerItem(deadJokers.getOrDefault(event.getPlayer(), 0)));
		event.getPlayer().updateInventory();
		deadJokers.remove(event.getPlayer());
	}


	private static void handleJoker(Player player) {
		if (!ForceItem.INSTANCE.getGameManager().isRunning()) return;

		player.sendMessage(ForceItem.getPrefix().append(Component.text("You skipped the Item ", NamedTextColor.GREEN))
				.append(Component.text(ForceItem.INSTANCE.getItemTable().getCurrentItem(player).getName(), NamedTextColor.AQUA)));
		ForceItem.INSTANCE.getItemTable().markAsSkipped(player, ForceItem.INSTANCE.getGameManager().getTime());
		ForceItem.INSTANCE.getGameManager().nextItem(player);
	}
}
