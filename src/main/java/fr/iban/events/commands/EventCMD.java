package fr.iban.events.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.iban.events.EventManager;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.EventType;
import fr.iban.events.menus.ArenaEditor;

public class EventCMD implements CommandExecutor, TabCompleter {

	private EventManager manager;


	public EventCMD(EventsPlugin plugin) {
		this.manager = plugin.getEventManager();

	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			if(args.length >= 1 && player.hasPermission("spartacube.events")) {
				switch (args[0].toLowerCase()) {
				case "start":
					if(args.length == 3) {
						EventType type = EventType.valueOf(args[1].toUpperCase());
						String arena = args[2];
						if(!manager.isRunning(type, arena)) {
							manager.runEvent(type, player, arena);
						}else {
							player.sendMessage("§cUne partie de "+ type.toString().toLowerCase() +" est déjà en cours dans cette arene.");
						}
					}
					break;
				case "arena":
					//event arena create eventtype nom
					if(args.length == 4) {
						EventType type = EventType.valueOf(args[2].toUpperCase());
						String name = args[3];
						if(args[1].equalsIgnoreCase("create")){
							if(!manager.getArenaNames(type).contains(name)) {
								manager.addArena(type, name);
								player.sendMessage("§aL'arène a bien été crée.");
							}else {
								player.sendMessage("§cUne arene à ce nom éxiste déjà.");
							}
						}else if(args[1].equalsIgnoreCase("edit")){
							if(manager.getArenaNames(type).contains(name)) {
								new ArenaEditor(player, type, name, manager).open();;
							}else {
								player.sendMessage("§cCette arene n'éxiste pas.");
							}
						}else {
							player.sendMessage("§c/event arena §4create/edit§c eventtype nom");
						}
					}else {
						player.sendMessage("§c/event arena §4create/edit eventtype nom");
					}
					break;

				default:
					break;
				}
			}
		}
		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> suggestions = new ArrayList<>();
		if(args.length == 1) {
			if("start".startsWith(args[0].toLowerCase())) {
				suggestions.add("start");
			}
			if("arena".startsWith(args[0].toLowerCase())) {
				suggestions.add("arena");
			}
		}
		if(args.length == 2) {
			switch (args[0].toLowerCase()) {
			case "start":
				for(EventType eventType: EventType.values()){
					if(eventType.toString().toLowerCase().startsWith(args[1].toLowerCase()))
						suggestions.add(eventType.toString().toLowerCase());
				}
				break;
			case "arena":
				if("create".startsWith(args[1].toLowerCase())) {
					suggestions.add("create");
				}
				if("edit".startsWith(args[1].toLowerCase())) {
					suggestions.add("edit");
				}
				break;
			default:
				break;
			}
		}
		if(args.length == 3) {
			if(args[0].toLowerCase().equals("arena")) {
				for(EventType type : EventType.values()) {
					if(type.toString().toLowerCase().startsWith(args[2].toLowerCase())) {
						suggestions.add(type.toString().toLowerCase());
					}
				}
			}else if(args[0].toLowerCase().equals("start")) {
				EventType type = EventType.valueOf(args[1].toUpperCase());
				for(String string : manager.getAvalaibleArenas(type)) {
					if(string.startsWith(args[2])) {
						suggestions.add(string);
					}
				}
			}
			
		}
		if(args.length == 4 && args[0].equalsIgnoreCase("arena") && args[1].equalsIgnoreCase("edit")) {
			EventType type = EventType.valueOf(args[2].toUpperCase());
			for(String string : manager.getArenaNames(type)) {
				if(string.startsWith(args[3])) {
					suggestions.add(string);
				}
			}	
		}
		return suggestions;
	}
}
