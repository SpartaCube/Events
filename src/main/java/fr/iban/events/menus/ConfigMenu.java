package fr.iban.events.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.menu.RewardSelectMenu;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.bukkitcore.utils.Head;
import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.common.teleport.EventAnnouce;
import fr.iban.events.Event;
import fr.iban.events.tasks.StartTask;

public class ConfigMenu extends Menu {
	
	private Event event;
	
	public ConfigMenu(Player player, Event event) {
		super(player);
		this.event = event;
	}

	@Override
	public String getMenuName() {
		return "§2Event §8> §a" + event.getName();
	}

	@Override
	public int getRows() {
		return 1;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();
		
		if(displayNameEquals(item, "§2§lRécompense")) {
			RewardsDAO.getTemplateRewardsAsync().thenAccept(rewards -> {
				Bukkit.getScheduler().runTask(core, () -> new RewardSelectMenu(player, rewards, reward -> {
					event.setReward(reward);
					open();
				}).open());
			});
		}else if(displayNameEquals(item, "§6§lAnnoncer")) {
			core.getRedisClient().getTopic("EventAnnounce").publish(new EventAnnouce(event.getName(), event.getArena(), event.getType().getDesc(), event.getWaitSLocation(), player.getName()));
		}else if(displayNameEquals(item, "§2§lLancer !")) {
			//if(event.getPlayers().size() > 1) {
				new StartTask(event).runTaskTimer(core, 0L, 20L);
//			}else {
//				player.sendMessage("§cIl faut plus d'un joueur pour lancer une partie !");
//			}
		}else {
			return;
		} 
	}

	@Override
	public void setMenuItems() {
		setFillerGlass();
		if(event.getReward() == null) {
			inventory.setItem(1, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense").addLore("§aPermet de choisir une récompense.").build());
		}else {
			inventory.setItem(1, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense").addLore("§aRécompense choisie : §2" + event.getReward().getName()).build());
		}
		inventory.setItem(7, new ItemBuilder(Material.PAPER).setName("§6§lAnnoncer").addLore("§aAnnonce l'event sur tout le serveur (1 toutes les 1 minutes maximum)").build());
		inventory.setItem(8, new ItemBuilder(Material.LIME_DYE).setName("§2§lLancer !").addLore("§aLance le jeu !").build());
	}

}
