package fr.iban.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.rewards.Reward;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.menus.ConfigMenu;

public abstract class Event {

	protected UUID host;
	protected List<UUID> players = new ArrayList<>();
	protected List<UUID> winners = new ArrayList<>();
	protected Menu menu;
	private String arena;
	protected GameState state = GameState.WAITING;
	protected EventManager manager;
	protected EventsPlugin plugin;
	protected Reward reward;


	public Event(EventsPlugin plugin, EventManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}

	
	public abstract Location getWaitingSpawnPoint();

	public abstract Location getStartPoint();

	public abstract SLocation getWaitSLocation();

	public void finish() {
		setGameState(GameState.FINISHED);
		//CoreBukkitPlugin.getInstance().getRedisClient().getTopic("EventAnnounce").publish(new EventAnnouce(getName(), getArena(), getType().getDesc(), null, null));
		if(reward != null) {
			for(UUID uuid : winners) {
				RewardsDAO.addRewardAsync(uuid.toString(), reward.getName(), reward.getServer(), reward.getCommand());
				Bukkit.getPlayer(uuid).sendMessage("§aVous avez reçu une récompense pour votre victoire ! (/recompenses)");
			}
		}
		
		for(UUID uuid : winners) {
			removePlayer(uuid);
		}
		
		manager.killEvent(this);
	}

	public abstract boolean isFinished();

	public abstract EventType getType();

	public void prepare(Player player, String arena) {
		host = player.getUniqueId();
		this.arena = arena;
		if(menu == null) {
			menu = new ConfigMenu(player, this);
		}
		menu.open();
		player.teleport(getWaitingSpawnPoint());
	}

	public List<UUID> getPlayers() {
		return players;
	}

	public String getName() {
		return getType().getName();
	}
	public UUID getHost() {
		return host;
	}

	public Menu getConfigMenu() {
		return menu;
	}

	public String getArena() {
		return arena;
	}

	public void start() {
		for(Player player : getViewers(100)) {
			if(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
				continue;
			addPlayer(player.getUniqueId());
		}
		setGameState(GameState.RUNNING);
	}

	public void addPlayer(UUID uuid) {
		if(state == GameState.WAITING) {
//			for(Player p : getViewers(50)){
//				p.sendMessage("§7" + Bukkit.getPlayer(uuid).getName() + " a rejoint l'event !");
//			}
			getPlayers().add(uuid);
		}
	}

	public void removePlayer(UUID uuid) {
		if(state == GameState.WAITING) {
			for(Player p : getViewers(50)){
				p.sendMessage("§7" + Bukkit.getPlayer(uuid).getName() + " a quitté l'event !");
			}
		}else {
			Player player = Bukkit.getPlayer(uuid);
			if(player != null) {
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
				player.teleport(getWaitingSpawnPoint());
				player.getInventory().clear();
				if(!player.getActivePotionEffects().isEmpty()) {
					for(PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
				}
			}
		}
		getPlayers().remove(uuid);
	}

	public GameState getGameState() {
		return state;
	}

	public void setGameState(GameState state) {
		this.state = state;
	}

	public Collection<Player> getViewers(int distance){
		return getStartPoint().getNearbyPlayers(distance);
	}

	public Reward getReward() {
		return reward;
	}

	public void setReward(Reward reward) {
		this.reward = reward;
	}
	
}