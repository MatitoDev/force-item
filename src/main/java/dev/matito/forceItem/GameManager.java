package dev.matito.forceItem;

import dev.matito.forceItem.util.ItemEntry;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

@Getter
public class GameManager {
	public boolean started = false;

	public boolean start() {
		started = true;
		ForceItem.INSTANCE.getTimer().startTimer();
		ForceItem.INSTANCE.getItemTable().getPlayersCurrentItems().forEach((player, itemEntry) -> {
			player.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			player.sendMessage(ForceItem.getPrefix().append(Component.text("THE GAME STARTS! GO! GO! GO!", NamedTextColor.GREEN)));
			player.sendMessage(ForceItem.getPrefix().append(Component.text("Your fist Item is: ", NamedTextColor.GREEN))
					.append(Component.text(itemEntry.getName(), NamedTextColor.AQUA)));
		});
		gameLoop();
		return true;
	}

	public boolean end() {
		return true;
	}

	private void gameLoop() {
		ForceItem.INSTANCE.getTimer().addTickLogic(time -> {
			checkInventory(time.toString());
			return null;
		});
	}

	private void checkInventory(String time) {
		ForceItem.INSTANCE.getItemTable().getPlayersCurrentItems().forEach((player, item) -> player.getInventory().forEach(invItem -> {
			if (invItem != null && invItem.getType().equals(item.getItemStack().getType())) nextItem(player, time);
		}));
	};

	private void nextItem(Player player, String time) {
		player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 255, 0.8f);
		player.sendMessage(ForceItem.getPrefix().append(Component.text("You completed the Item ", NamedTextColor.GREEN))
				.append(Component.text(ForceItem.INSTANCE.getItemTable().getCurrentItem(player).getName(), NamedTextColor.AQUA)));
		ForceItem.INSTANCE.getItemTable().markAsDone(player, time);
		ItemEntry newItem = getNewItem();
		ForceItem.INSTANCE.getItemTable().add(player, newItem);
		player.sendMessage(ForceItem.getPrefix().append(Component.text("Your next Item is ", NamedTextColor.GREEN))
				.append(Component.text(newItem.getName(), NamedTextColor.AQUA)));
	}

	public static ItemEntry getNewItem() {
		double r = new Random().nextDouble();
		return ItemEntry.getRandomItemByDifficultyWithoutDimension(ItemEntry.Dimension.END, r < 0.3 ? 0 : r < 0.6 ? 1 : r < 0.8 ? 2 : 3);
	}
}

