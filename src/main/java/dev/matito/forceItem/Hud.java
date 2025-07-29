package dev.matito.forceItem;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Hud {

	private Map<Player, BossBar> bossBars = new HashMap<>();

	public void showBossBar(Player player) {
		if (bossBars.containsKey(player)) {
			bossBars.get(player).name(Component.text(ForceItem.INSTANCE.getItemTable().getCurrentItemEntry(player).getName()));
		} else {
			BossBar bossBar = BossBar.bossBar(Component.text(ForceItem.INSTANCE.getItemTable().getCurrentItemEntry(player).getName()), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
			bossBars.put(player, bossBar);
			player.showBossBar(bossBar);
		}
	}

	public void hideBossBar(Player player) {
		if (bossBars.containsKey(player)) {
			player.hideBossBar(bossBars.get(player));
			bossBars.remove(player);
		}
	}

	public void removeAllBossBars() {
		bossBars.forEach(Audience::hideBossBar);
		bossBars.clear();
	}
}
