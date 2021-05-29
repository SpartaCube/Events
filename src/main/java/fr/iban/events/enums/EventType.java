package fr.iban.events.enums;

import java.util.List;

import fr.iban.events.options.Option;
import fr.iban.events.sumotori.SumotoriEvent;

public enum EventType {
	
	SUMOTORI("Sumotori", "Tous les joueurs s'affrontent dans une arène, l'objectif et de faire valser les joueurs hors de celle-ci. Le dernier joueur à y subsister sera déclaré vainqueur.", SumotoriEvent.getArenaOptions());
	
	private String name;
	private String desc;
	private List<Option> arenaOptions;
	
	EventType(String name, String desc, List<Option> arenaOptions) {
		this.name = name;
		this.desc = desc;
		this.arenaOptions = arenaOptions;
	}

	public String getName() {
		return name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public List<Option> getArenaOptions() {
		return arenaOptions;
	}

}
