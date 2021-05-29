package fr.iban.events.options;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationOption extends Option {

	private Location locationValue;
	
	public LocationOption(String name) {
		super(name);
		locationValue = Bukkit.getWorld("world").getSpawnLocation();
	}
	
	public Location getLocationValue() {
		return locationValue;
	}
	
	public void setLocationValue(Location locationValue) {
		this.locationValue = locationValue;
	}

}
