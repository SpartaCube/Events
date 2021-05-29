package fr.iban.events.sumotori;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.Event;
import fr.iban.events.EventManager;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.MoveBlockListener;
import fr.iban.events.interfaces.PlayerDamageListener;
import fr.iban.events.menus.ConfigMenu;
import fr.iban.events.options.IntOption;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;

public class SumotoriEvent extends Event implements MoveBlockListener, PlayerDamageListener {

	public SumotoriEvent(EventsPlugin plugin, EventManager manager) {
		super(plugin, manager);
	}

	@Override
	public EventType getType() {
		return EventType.SUMOTORI;
	}

	public void prepare(Player player, String arena) {
		menu = new ConfigMenu(player, this);
		super.prepare(player, arena);
	}

	@Override
	public void start() {
		super.start();

		for(UUID uuid : players) {
			Player player = Bukkit.getPlayer(uuid);
//			PlayerInventory inv = player.getInventory();
//			inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
//			inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
//			inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
//			inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
//			player.updateInventory();
			player.teleport(getStartPoint());
			player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagné !", 10, 70, 20);
			player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Tone.A));
			player.setGameMode(GameMode.ADVENTURE);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
//			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
			player.getInventory().addItem(new ItemBuilder(Material.STICK).setName("§").addEnchant(Enchantment.KNOCKBACK, 1).build());
			player.setMaximumNoDamageTicks(15);
		}
	}

	@Override
	public void finish() {
		state = GameState.FINISHED;
		UUID winner = getPlayers().get(0);
		winners.add(winner);
		for(Player p : getViewers(50)){
			p.sendMessage("§2§lLa partie est terminée, " + Bukkit.getPlayer(winner).getName() + " a gagné !");
		}
		super.finish();
	}

	@Override
	public boolean isFinished() {
		return getPlayers().size() <= 1;
	}

	public static List<Option> getArenaOptions() {
		List<Option> list = new ArrayList<>();
		list.add(new LocationOption("game-start-location"));
		list.add(new LocationOption("waiting-location"));
		list.add(new IntOption("sumo-height", 50));
		return list;
	}

	@Override
	public Location getWaitingSpawnPoint() {
		LocationOption locopt = (LocationOption)manager.getArenaOptions(getType(), getArena()).get(1);
		return locopt.getLocationValue();
	}

	@Override
	public SLocation getWaitSLocation() {
		Location loc = getWaitingSpawnPoint();
		SLocation sloc = new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
		return sloc;
	}

	@Override
	public void onMoveBlock(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		int y = ((IntOption)manager.getArenaOptions(getType(), getArena()).get(2)).getIntValue();
		if(e.getTo() .getY() < y) {
			removePlayer(player.getUniqueId());
		}
	}

	@Override
	public Location getStartPoint() {
		LocationOption locopt = (LocationOption)manager.getArenaOptions(getType(), getArena()).get(0);
		return locopt.getLocationValue();
	}

	@Override
	public void removePlayer(UUID uuid) {
		super.removePlayer(uuid);
		Player player = Bukkit.getPlayer(uuid);
		if(state == GameState.RUNNING) {
				if(!isFinished()) {
					for(Player p : getViewers(50)){
						p.sendMessage("§7" + player.getName() + " est éliminé ! Plus que " + getPlayers().size() + " joueurs restants.");
					}
				}else {
					finish();
				}
		}
		player.setMaximumNoDamageTicks(20);
	}

	@Override
	public void onPlayerDamage(EntityDamageEvent e) {
		e.setDamage(0);
	}

}
