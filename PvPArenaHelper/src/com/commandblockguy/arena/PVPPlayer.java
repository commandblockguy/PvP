package com.commandblockguy.arena;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class PVPPlayer {
	
	public UUID id;
	public Location base;
	public PVPEvent currentEvent;
	public Team team;
	
	public PVPPlayer(Player player) {
		this.id = player.getUniqueId();
	}
	
	public void setBase(Location loc) {
		this.base = loc;
	}
	public void setBaseHere() {
		setBase(getPlayer().getLocation());
	}
	public void setEvent(PVPEvent event) {
		leaveEvent();
		currentEvent = event;
		currentEvent.addPlayer(this);
	}
	public void setTeam(Team team) {
		this.team = team;
	}
	public void leaveEvent() {
		if(currentEvent != null)
			currentEvent.removePlayer(this);
		currentEvent = null;
	}
	public boolean shouldKeepInv() {
		return participating();// && currentEvent.roundInProgress();
	}
	public boolean participating() {
		if(currentEvent == null) return false;
		return inArena(currentEvent.getTown());
	}
	public boolean participating(PVPEvent event) {
		return currentEvent == event && standingInTown(event.getTown());
	}
	public Resident getResident() {
		try {
			return TownyUniverse.getDataSource().getResident(getPlayer().getName());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean standingInTown(Town town) {
		try {
			return WorldCoord.parseWorldCoord(getPlayer()).getTownBlock().getTown() == town;
		} catch (NotRegisteredException e) {
			return false;
		}
	}
	public boolean inArena(Town town) {
		if(!standingInTown(town)) return false;
		try {
			if(!WorldCoord.parseWorldCoord(getPlayer()).getTownBlock().getType().equals(TownBlockType.ARENA)) return false;
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Player getPlayer() {
		return getOfflinePlayer().getPlayer();
	}
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(id);
	}
	public PVPEvent getEvent() {
		return currentEvent;
	}
	public Location getBase() {
		return base;
	}
	public Team getTeam() {
		return team;
	}
	
	public void tpToBase() {
		if(base == null) {
			getPlayer().sendMessage("No base is set! Set one with \"/pvp base set\" at the correct location.");
		} else { 
			getPlayer().teleport(base);
			getPlayer().sendMessage("Whoosh!");
		}
	}

	public TeamColor getTeamColor() {
		if(team != null)
			return team.getColor();
		return TeamColor.NONE;
	}
}
