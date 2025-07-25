package dev.matito.forceItem;

import dev.matito.forceItem.util.ItemEntry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Getter
public class GameManager {
	public boolean running = false;
	String time = "";

	public boolean start() {
		running = true;
		ForceItem.INSTANCE.getTimer().startTimer();
		ForceItem.INSTANCE.getItemTable().getPlayersCurrentItems().forEach((player, itemEntry) -> {
			player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			player.sendMessage(ForceItem.getPrefix().append(Component.text("THE GAME STARTS! GO! GO! GO!", NamedTextColor.GREEN)));
			player.sendMessage(ForceItem.getPrefix().append(Component.text("Your first Item is: ", NamedTextColor.GREEN))
					.append(Component.text(itemEntry.getName(), NamedTextColor.AQUA)));
		});
		gameLoop();
		return true;
	}

	public boolean end() {
		if (!running) return false;
		running = false;
		ForceItem.INSTANCE.getServer().sendMessage(ForceItem.getPrefix().append(Component.text("THE GAME STOPPED! ", NamedTextColor.RED)));
		return true;
	}

	private void gameLoop() {
		ForceItem.INSTANCE.getTimer().addTickLogic(time -> {
			this.time = time.toString();
			ForceItem.INSTANCE.getItemTable().getPlayersCurrentItems().forEach((player, item) -> {
				if (player != null && Arrays.stream(player.getInventory().getContents()).anyMatch(invItem -> invItem != null && invItem.getType().equals(item.getItemStack().getType()))) {
					player.sendMessage(ForceItem.getPrefix().append(Component.text("You completed the Item ", NamedTextColor.GREEN))
							.append(Component.text(ForceItem.INSTANCE.getItemTable().getCurrentItem(player).getName(), NamedTextColor.AQUA)));
					nextItem(player);
				}
			});


			if (!ForceItem.INSTANCE.getTimer().getTimerStatus()) end();
			return null;
		});
	}

	public void nextItem(Player player) {
		player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 255, 0.8f);
		ForceItem.INSTANCE.getItemTable().markAsDone(player, time);
		ItemEntry newItem = getNewItem(player);
		ForceItem.INSTANCE.getItemTable().add(player, newItem);
		player.sendMessage(ForceItem.getPrefix().append(Component.text("Your next Item is ", NamedTextColor.GREEN))
				.append(Component.text(newItem.getName(), NamedTextColor.AQUA)));
	}

	public static ItemEntry getNewItem(Player player) {
		double r = new Random().nextDouble();
		ItemEntry item = ItemEntry.getRandomItemByDifficultyWithoutDimension(ItemEntry.Dimension.END, r < 0.3 ? 0 : r < 0.6 ? 1 : r < 0.8 ? 2 : 3);
		for (ItemEntry itemEntry : ForceItem.INSTANCE.getItemTable().getFinishedItems(player)) if (itemEntry.equals(item)) return getNewItem(player);
		for (ItemEntry itemEntry : ForceItem.INSTANCE.getSkippedItems()) if (itemEntry.equals(item)) return getNewItem(player);
		return item;
	}
}

