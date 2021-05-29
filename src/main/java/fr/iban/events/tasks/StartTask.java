package fr.iban.events.tasks;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import fr.iban.events.Event;

public class StartTask extends BukkitRunnable {

	private int timer = 10;
	private Event event;

	public StartTask(Event event) {
		this.event = event;
	}

	@Override
	public void run() {

		if(timer == 10 || timer == 5 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1) {
			event.getViewers(50).forEach(p -> {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
				p.sendMessage("§aL'event va commencer dans " + timer + " secondes !");
			});
		}

		if(timer == 0) {
			event.start();
			cancel();
		}

		timer--;
	}

}
