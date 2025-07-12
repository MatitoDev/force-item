package dev.matito.pluginTemplateMatito.listener;

import dev.matito.pluginTemplateMatito.PluginTemplateMatito;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class TemplateListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		BukkitAudiences.create(PluginTemplateMatito.INSTANCE).players()
				.sendMessage(PluginTemplateMatito.getPrefix().append(event.getPlayer().displayName().append(Component.text(" joined the server!"))));
	}

}
