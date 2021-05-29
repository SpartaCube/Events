package fr.iban.events.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.iban.events.Event;
import fr.iban.events.EventsPlugin;
import fr.iban.events.interfaces.MoveBlockListener;

public class PlayerMoveListener implements Listener {
	
	private EventsPlugin plugin;

	public PlayerMoveListener(EventsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		final Location from = e.getFrom();
		final Location to = e.getTo();


		int x = Math.abs(from.getBlockX() - to.getBlockX());
		int y = Math.abs(from.getBlockY() - to.getBlockY());
		int z = Math.abs(from.getBlockZ() - to.getBlockZ());

		if (x == 0 && y == 0 && z == 0) return;
		
		Event event = plugin.getEventManager().getPlayingEvent(player);
		
		if(!(event instanceof MoveBlockListener)) return;
		
		((MoveBlockListener) event).onMoveBlock(e);
		
	}

}
