package com.commandblockguy.arena;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import com.commandblockguy.arena.commands.PVPCommand;
import com.commandblockguy.arena.listeners.DeathListener;
import com.commandblockguy.arena.listeners.InventoryListener;


public class Main extends JavaPlugin {

	public static Main plugin;
	public static HashMap<UUID, PVPPlayer> playerMap = new HashMap<UUID, PVPPlayer>();
	public static HashMap<String, PVPEvent> eventMap = new HashMap<String, PVPEvent>();
	
	@Override
	public void onEnable() {
		plugin = this;
		this.getCommand("pvp").setExecutor(new PVPCommand());
		this.getServer().getPluginManager().registerEvents(new DeathListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static PVPPlayer getPlayer(OfflinePlayer player) {
		UUID id = player.getUniqueId();
		if(playerMap.containsKey(id))
			return playerMap.get(id);
		return null;
	}
	public static void setPlayer(PVPPlayer player) {
		playerMap.put(player.getPlayer().getUniqueId(), player);
	}
	public static PVPEvent getEvent(String townName) {
		if(eventMap.containsKey(townName.toLowerCase()))
			return eventMap.get(townName.toLowerCase());
		return null;
	}
	public static void setEvent(PVPEvent event) {
		eventMap.put(event.getTown().getName().toLowerCase(), event);
	}
	public static void endEvent(String town) {
		eventMap.remove(town);
	}
}
