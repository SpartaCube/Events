package fr.iban.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.iban.events.commands.EventCMD;
import fr.iban.events.commands.HostCMD;
import fr.iban.events.listeners.DamageListeners;
import fr.iban.events.listeners.FoodListener;
import fr.iban.events.listeners.JoinQuitListeners;
import fr.iban.events.listeners.PlayerMoveListener;
import fr.iban.events.listeners.TeleportListener;

public final class EventsPlugin extends JavaPlugin {

	private EventManager eventManager;
	private static EventsPlugin instance;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		eventManager = new EventManager(this);
		getCommand("event").setExecutor(new EventCMD(this));
		getCommand("host").setExecutor(new HostCMD(this));
		registerListeners(new PlayerMoveListener(this),
				new JoinQuitListeners(getEventManager()),
				new TeleportListener(eventManager),
				new DamageListeners(eventManager),
				new JoinQuitListeners(eventManager), new FoodListener());
	}

	@Override
	public void onDisable() {
		saveConfig();
	}

	private void registerListeners(Listener... listeners) {

		PluginManager pm = Bukkit.getPluginManager();

		for (Listener listener : listeners) {
			pm.registerEvents(listener, this);
		}

	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public static EventsPlugin getInstance() { return instance; }

}
