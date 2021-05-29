package fr.iban.events.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.iban.events.Event;
import fr.iban.events.EventManager;

public class JoinQuitListeners implements Listener {

	private EventManager manager;

	public JoinQuitListeners(EventManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if(!player.hasPermission("event.admin")) {
			player.setGameMode(GameMode.ADVENTURE);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Event event = manager.getPlayingEvent(player);
		if(event != null) {
			event.removePlayer(player.getUniqueId());
		}
	}

}
