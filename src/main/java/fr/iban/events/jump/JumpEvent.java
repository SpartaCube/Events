package fr.iban.events.jump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.Event;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.MoveBlockListener;
import fr.iban.events.interfaces.PlayerDamageListener;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;

public class JumpEvent extends Event implements MoveBlockListener, PlayerDamageListener {

	private Map<UUID, Location> checkpoints = new HashMap<>();
	private boolean finished = false;

	public JumpEvent(EventsPlugin plugin) {
		super(plugin);
	}

	@Override
	public void start() {
		super.start();

		for(UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
			player.setCollidable(false);
			player.teleport(getStartPoint());
			player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagné !", 10, 70, 20);
			player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Tone.A));
		}
	}

	@Override
	public Location getWaitingSpawnPoint() {
		LocationOption locopt = (LocationOption)manager.getArenaOptions(getType(), getArena()).get(0);
		return locopt.getLocationValue();
	}

	@Override
	public Location getStartPoint() {
		LocationOption locopt = (LocationOption)manager.getArenaOptions(getType(), getArena()).get(1);
		return locopt.getLocationValue();
	}

	public Location getEndPoint() {
		LocationOption locopt = (LocationOption)manager.getArenaOptions(getType(), getArena()).get(2);
		return locopt.getLocationValue();
	}

	@Override
	public SLocation getWaitSLocation() {
		Location loc = getWaitingSpawnPoint();
		return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public EventType getType() {
		return EventType.JUMP;
	}

	public static List<Option> getArenaOptions() {
		List<Option> list = new ArrayList<>();
		list.add(new LocationOption("waiting-location"));
		list.add(new LocationOption("game-start-location"));
		list.add(new LocationOption("game-end-location"));
		return list;
	}

	@Override
	public void onMoveBlock(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location to = e.getTo();
		Block toBlock = to.getBlock();

		if(toBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			if(!getCheckPoint(player).getBlock().getLocation().equals(toBlock.getLocation())) {
				checkpoints.put(player.getUniqueId(), toBlock.getLocation());
				player.sendMessage("§aVous avez atteint un checkpoint.");
			}
		}

		if(to.distanceSquared(getEndPoint()) <= 1 ) {

			if(!isFinished()) {
				if(winReward != null) {
					RewardsDAO.addRewardAsync(player.getUniqueId().toString(), winReward.getName(), winReward.getServer(), winReward.getCommand());
					Bukkit.getPlayer(player.getUniqueId()).sendMessage("§aVous avez reçu une récompense pour votre victoire ! (/recompenses)");
				}
				winners.add(player.getUniqueId());
				finished = true;
			}
			
			for(Player p : getViewers(250)){
				p.sendMessage("§2§l" + player.getName() + " a atteint l'arrivée !");
			}
			removePlayer(player.getUniqueId());
			if(players.isEmpty()) {
				finish();
			}

		}

		if(player.getFallDistance() >= 10) {
			player.teleport(getCheckPoint(player));
		}
	}

	@Override
	public void finish() {
		setGameState(GameState.FINISHED);

		manager.killEvent(this);	}

	private Location getCheckPoint(Player player) {
		return checkpoints.getOrDefault(player.getUniqueId(), getStartPoint());
	}

	@Override
	public void onPlayerDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

}
