package dev.matito.forceItem.listener;

import dev.matito.forceItem.ForceItem;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class TemplateListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitAudiences.create(ForceItem.INSTANCE).players()
				.sendMessage(ForceItem.getPrefix().append(event.getPlayer().displayName().append(Component.text(" joined the server!"))));
	}

}
