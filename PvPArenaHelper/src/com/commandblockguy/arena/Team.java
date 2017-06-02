package com.commandblockguy.arena;

import java.util.ArrayList;

import org.bukkit.Location;

public class Team {
	
	public ArrayList<PVPPlayer> players = new ArrayList<PVPPlayer>();
	public TeamColor color;
	public Location ctfBaseChest = null;
	public Team otherTeam;
	public PVPEvent event;
	
	public Team(PVPEvent event, TeamColor color) {
		this.color = color;
		this.event = event;
	}
	
	public void setChest(Location pos) {
		ctfBaseChest = pos;
	}
	public void addPlayer(PVPPlayer player) {
		players.add(player);
	}
	public void removePlayer(PVPPlayer player) {
		players.remove(player);
	}
	public void setOtherTeam(Team otherTeam) {
		this.otherTeam = otherTeam;
	}
	
	
	public TeamColor getColor() {
		return color;
	}
	public ArrayList<PVPPlayer> getPlayers() {
		return players;
	}
	public PVPEvent getEvent() {
		return event;
	}

	public Location getChest() {
		return ctfBaseChest;
	}

	public boolean hasChest() {
		return ctfBaseChest != null;
	}
	
}
