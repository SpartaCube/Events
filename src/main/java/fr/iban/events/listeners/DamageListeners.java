package fr.iban.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.iban.events.Event;
import fr.iban.events.EventManager;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.PlayerDamageListener;

public class DamageListeners implements Listener {
	
	private EventManager manager;

	public DamageListeners(EventManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player)e.getEntity();
			Event event = manager.getPlayingEvent(player);
			if(event == null || event.getGameState() == GameState.WAITING) {
				e.setCancelled(true);
			}else {
				System.out.println(event.getName() + "");
				if(event instanceof PlayerDamageListener) {
					((PlayerDamageListener)event).onPlayerDamage(e);
				}
			}
		}
	}
	
//	private Player getPlayerDamager(EntityDamageByEntityEvent event) {
//		Player player = null;
//		if(event.getCause() == DamageCause.PROJECTILE && event.getDamager() instanceof Projectile) {
//			Projectile projectile = (Projectile) event.getDamager();
//			if(projectile.getShooter() instanceof Player) {
//				player = (Player)projectile.getShooter();
//			}
//		}
//		if(event.getDamager() instanceof Player) {
//			player = (Player) event.getDamager();
//		}
//		return player;
//	}
	

}
