package fr.iban.events.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.EventManager;
import fr.iban.events.enums.EventType;
import fr.iban.events.options.IntOption;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import fr.iban.events.options.StringOption;

public class ArenaEditor extends Menu {

	private EventManager manager;
	private EventType type;
	private String name;
	private List<Option> options;
	private Map<Integer, Option> optionAtSlot = new HashMap<>();

	public ArenaEditor(Player player, EventType type, String name, EventManager manager) {
		super(player);
		this.type = type;
		this.name = name;
		this.manager = manager;
		this.options = manager.getArenaOptions(type, name);
	}

	
	//TODO options d'arene et non celles par defaut.
	
	@Override
	public String getMenuName() {
		return "§2Edition d'arene";
	}

	@Override
	public int getRows() {
		return 1 + options.size()/9;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		//sItemStack item = e.getCurrentItem();
		CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();

		if(optionAtSlot.containsKey(e.getSlot())) {
			Option option = optionAtSlot.get(e.getSlot());
			if(option instanceof IntOption) {
				IntOption intoption = (IntOption)option;
				player.sendMessage("§e§lVeuillez entrer un nombre entier.");
				core.getTextInputs().put(player.getUniqueId(), texte -> {
					try {
						intoption.setIntValue(Integer.parseInt(texte));
						manager.saveArenaOption(type, name, intoption);
						player.sendMessage("Option sauvegardée.");
						core.getTextInputs().remove(player.getUniqueId());
						open();
					} catch (NumberFormatException e1) {
						player.sendMessage("§cVous devez entrer un nombre entier.");
					}
				});
			}else if(option instanceof StringOption) {
				StringOption stroption = (StringOption)option;
				core.getTextInputs().put(player.getUniqueId(), texte -> {
					stroption.setStringValue(texte);
					manager.saveArenaOption(type, name, stroption);
					player.sendMessage("Option sauvegardée.");
					open();
				});
			}else if(option instanceof LocationOption) {
				LocationOption locoption = (LocationOption)option;
				locoption.setLocationValue(player.getLocation());
				manager.saveArenaOption(type, name, locoption);
				player.sendMessage("Option sauvegardée.");
				open();
			}	
		}
	}

	@Override
	public void setMenuItems() {
		for(int i = 0 ; i < options.size() ; i++) {
			Option option = options.get(i);
			inventory.setItem(i, buildItem(option));
			optionAtSlot.put(i, option);
		}
	}
	
	private ItemStack buildItem(Option option) {
		ItemBuilder ib = new ItemBuilder(Material.PAPER).setName(option.getName());
		if(option instanceof IntOption) {
			IntOption intoption = (IntOption)option;
			ib.addLore("" + intoption.getIntValue());
		}else if(option instanceof StringOption) {
			StringOption stroption = (StringOption)option;
			ib.addLore(stroption.getStringValue());
		}else if(option instanceof LocationOption) {
			LocationOption locoption = (LocationOption)option;
			Location loc = locoption.getLocationValue();
			if(loc != null) {
				ib.addLore("Monde :" + loc.getWorld().getName());
				ib.addLore("X :" + loc.getX());
				ib.addLore("Y :" + loc.getY());
				ib.addLore("Z :" + loc.getZ());
			}else {
				ib.addLore("Non défini");
			}
		}
		return ib.build();
	}

}
