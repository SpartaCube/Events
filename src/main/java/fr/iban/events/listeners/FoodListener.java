package fr.iban.events.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodListener implements Listener {

	  @EventHandler
	   public void onHungerDeplete(FoodLevelChangeEvent e) {
	     e.setCancelled(true);
	     e.setFoodLevel(20);
	   }
	
}
