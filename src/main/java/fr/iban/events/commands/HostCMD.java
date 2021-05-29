package fr.iban.events.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.iban.events.Event;
import fr.iban.events.EventManager;
import fr.iban.events.EventsPlugin;

public class HostCMD implements CommandExecutor {

	private EventManager manager;

	//private EventsPlugin plugin;

	public HostCMD(EventsPlugin plugin) {
		//this.plugin = plugin;
		this.manager = plugin.getEventManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args.length == 0 && !manager.getRunningEvents().isEmpty()) {
				for(Event event : manager.getRunningEvents()) {
					if(event.getHost().equals(player.getUniqueId())) {
						event.getConfigMenu().open();
						break;
					}
				}
			}
		}
		return false;
	}

}
