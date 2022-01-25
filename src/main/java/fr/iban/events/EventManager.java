package fr.iban.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import fr.mrlaikz.spartaflag.FlagEvent;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.jump.JumpEvent;
import fr.iban.events.options.IntOption;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import fr.iban.events.options.StringOption;
import fr.iban.events.sumotori.SumotoriEvent;

public class EventManager {


	private List<Event> runningEvents = new ArrayList<>();
	private EventsPlugin plugin;
	private FileConfiguration config;

	public EventManager(EventsPlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
	}


	public List<Event> getRunningEvents() {
		return runningEvents;
	}


	/**
	 * Vérifie si un event est en cours à l'arène donnée
	 * @param type - type d'event
	 * @param arena - nom de l'arene
	 * @return - si l'event est en cours.
	 */
	public boolean isRunning(EventType type, String arena) {
		return !runningEvents.isEmpty() && !getAvalaibleArenas(type).contains(arena);
	}

	/**
	 * Débuter un event
	 * @param type - type d'evenement.
	 * @param player - joueur qui hoste la partie.
	 */
	public void runEvent(EventType type, Player player, String arena) {
		Event event = null;
		switch (type) {
		case SUMOTORI:
			event = new SumotoriEvent(plugin);
			break;
		case JUMP:
			event = new JumpEvent(plugin);
			break;
		case CAPTURE_THE_FLAG:
			event = new FlagEvent(plugin);
		default:
			break;
		}
		if(event != null) {
			runningEvents.add(event);
			event.prepare(player, arena);
		}
	}

	public void killEvent(Event event) {
		getRunningEvents().remove(event);
		System.out.println(getRunningEvents().size());
	}

	/**
	 * Vérifie si un joueur est entrain de jouer à un event.
	 * @param player
	 * @return - event
	 */
	public boolean isPlaying(Player player) {
		return getPlayingEvent(player) != null;
	}


	public void joinEvent(Player player, Event event) {

		if(event == null
				|| player.getGameMode() == GameMode.CREATIVE 
				|| player.getGameMode() == GameMode.SPECTATOR
				|| isPlaying(player)
				|| event.getGameState() == GameState.RUNNING) {
			return;
		}

		event.addPlayer(player.getUniqueId());
	}

	/**
	 * Renvois l'event auquel le joueur joue actuellement.
	 * @param player
	 * @return - event ou null si non trouvé.
	 */
	@Nullable
	public Event getPlayingEvent(Player player) {
		for(Event event : runningEvents) {
			for(UUID uuid : event.getPlayers()) {
				if(uuid.toString().equals(player.getUniqueId().toString())) {
					return event;
				}
			}
		}
		return null;
	}

	@Nullable
	public Event getNearestEvent(Player player) {
		Event event = null;
		for(Event ev : runningEvents) {
			double evDistance = ev.getWaitingSpawnPoint().distanceSquared(player.getLocation());
			if(evDistance < 10000 && (event == null || evDistance < event.getWaitingSpawnPoint().distanceSquared(player.getLocation()))) {
				event = ev;
			}
		}
		return event;
	}

	public List<String> getArenaNames(EventType type){
		List<String> list = new ArrayList<>();
		if(config.getConfigurationSection(type.toString().toLowerCase()) != null) {
			for(String name : config.getConfigurationSection(type.toString().toLowerCase()).getKeys(false)) {
				list.add(name);
			}	
		}
		return list;
	}

	public List<String> getAvalaibleArenas(EventType type) {
		List<String> list = getArenaNames(type);
		//On enlève les arènes en cours d'utilisation.
		for(Event event : runningEvents.stream().filter(e -> e.getType() == type).collect(Collectors.toList())) {
			if(list.contains(event.getArena())) {
				list.remove(event.getArena());
			}
		}
		return list;
	}

	public List<Option> getArenaOptions(EventType event, String arenaName){
		List<Option> list = new ArrayList<>();
		String path = event.toString().toLowerCase() + "." + arenaName + ".";
		for(Option option : event.getArenaOptions()) {
			list.add(option);
			if(option instanceof IntOption) {
				((IntOption) option).setIntValue(config.getInt(path+option.getName()));
			}else if(option instanceof StringOption) {
				((StringOption) option).setStringValue(config.getString(path+option.getName()));
			}else if(option instanceof LocationOption) {
				((LocationOption) option).setLocationValue(config.getLocation(path+option.getName()));
			}
		}
		return list;
	}

	public void saveArenaOption(EventType type, String arenaName, Option option) {
		String path = type.toString().toLowerCase() + "." + arenaName + "." + option.getName();
		if(option instanceof IntOption) {
			config.set(path, ((IntOption) option).getIntValue());
		}else if(option instanceof StringOption) {
			config.set(path, ((StringOption) option).getStringValue());
		}else if(option instanceof LocationOption) {
			config.set(path, ((LocationOption) option).getLocationValue());
		}    
	}

	public void addArena(EventType type, String arenaName) {
		String path = type.toString().toLowerCase() + "." + arenaName;
		config.createSection(path);
	}

	public void deleteArena(EventType type, String arenaName) {
		String path = type.toString().toLowerCase() + "." + arenaName;
		config.set(path, null);
	}

}
