package com.commandblockguy.arena;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;

import com.palmergames.bukkit.towny.object.Town;

public class PVPEvent {

	public ArrayList<Round> rounds = new ArrayList<Round>();
	public Town town;
	public String name;
	public ArrayList<PVPPlayer> players = new ArrayList<PVPPlayer>();
	public Team[] teams = {new Team(this, TeamColor.RED), new Team(this, TeamColor.BLUE)};
	public TeamSelection selectionType = TeamSelection.SELECT;
	public boolean teamsRandomized = false;
	public EventType type = EventType.OTHER;
	public PVPPlayer admin;
	
	public PVPEvent(Town town) {
		this.town = town;
	}
	public PVPEvent(Town town, String name) {
		this(town);
		this.name = name;
	}
	
	public Town getTown() {
		return town;
	}
	public ArrayList<Round> getRounds() {
		return rounds;
	}
	public String getEventName() {
		return name;
	}
	
	public String getTownName() {
		return town.getName();
	}
	public Round getRound(int roundNumber) {
		return rounds.get(roundNumber);
	}
	public Round currentRound() {
		if(roundInProgress())
			return rounds.get(rounds.size() - 1);
		return null;
	}
	public boolean roundInProgress() {
		if(rounds.size() == 0)
			return false;
		return rounds.get(rounds.size() - 1).inProgress();
	}
	public void endRound() {
		rounds.get(rounds.size() - 1).end();
	}
	public void addRound(String name) {
		rounds.add(new Round(this, name));
	}
	public void startRound() {
		rounds.get(rounds.size() - 1).startWithDelay();
	}
	public void startRound(int delay) {
		rounds.get(rounds.size() - 1).startWithDelay(delay);
	}
	public void startRound(int delay, int length) {
		rounds.get(rounds.size() - 1).startWithDelay(delay, length);
	}
	public void messagePlayers(String msg) {
		for(PVPPlayer player : players) {
			player.getPlayer().sendMessage(msg);
		}
	}
	public void addPlayer(PVPPlayer player) {
		players.add(player);
		messagePlayers(player.getPlayer().getName() + " joined the PvP event!");
		if((selectionType == TeamSelection.RANDOM || selectionType == TeamSelection.RERANDOM) && !roundInProgress()) {
			messagePlayers("Randomizing teams because of new player...");
			randomizeTeams();
		}
	}
	public void removePlayer(PVPPlayer player) {
		players.remove(player);
		teams[0].removePlayer(player);
		teams[1].removePlayer(player);
	}
	public void setTeam(PVPPlayer player, TeamColor color) {
		teams[0].removePlayer(player);
		teams[1].removePlayer(player);
		if(color.equals(TeamColor.RED)) {
			teams[0].addPlayer(player);
			player.setTeam(teams[0]);
			messagePlayers(player.getPlayer().getName() + " has joined the " + ChatColor.RED + "RED team!");
		}
		if(color.equals(TeamColor.BLUE)) {
			teams[1].addPlayer(player);
			player.setTeam(teams[1]);
			messagePlayers(player.getPlayer().getName() + " has joined the " + ChatColor.BLUE + "BLUE team!");
		}
	}
	public void randomizeTeams() {
		Collections.shuffle(players);
		int redSize = players.size();
		if(Math.random() > 0.5) {
			redSize = (int) Math.ceil(((double) redSize) / 2);
		} else {
			redSize = (int) Math.floor(((double) redSize) / 2);
		}
		for(int i = 0; i < players.size(); i++) {
			if(i < redSize) {
				setTeam(players.get(i), TeamColor.RED);
			} else {
				setTeam(players.get(i), TeamColor.BLUE);
			}
		}
	}
	public void onRoundEnd() {
		
	}
	public void setSelectionType(TeamSelection type) {
		this.selectionType = type;
	}
	public TeamSelection getSeletectionType() {
		return selectionType;
	}
	public void setEventType(EventType type) {
		this.type = type;
	}
	public EventType getEventType() {
		return type;
	}
	public Team getRedTeam() {
		return teams[0];
	}
	public Team getBlueTeam() {
		return teams[1];
	}
	public boolean chestsSet() {
		return teams[0].hasChest() && teams[0].hasChest();
	}
	public boolean allHaveTeam() {
		for(PVPPlayer p : players) {
			if(p.getTeam() == null) return false;
		}
		return true;
	}
	public PVPPlayer getAdmin() {
		return admin;
	}
	public void setAdmin(PVPPlayer player) {
		admin = player;
	}
}
