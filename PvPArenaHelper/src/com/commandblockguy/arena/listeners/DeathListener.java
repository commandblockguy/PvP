package com.commandblockguy.arena.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.commandblockguy.arena.Main;
import com.commandblockguy.arena.PVPPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class DeathListener implements Listener {
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		PVPPlayer player = Main.getPlayer(event.getEntity());
		if(player == null) return;
		if(event.getEntity().getKiller() != null && player.participating())
			Bukkit.broadcastMessage(ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED+ " KILLED " + ChatColor.RED + event.getEntity().getName());
		if(player.shouldKeepInv()) {
			event.setKeepInventory(true);
			event.setKeepLevel(true);
			Inventory inventory = event.getEntity().getInventory();
			HashMap<Integer, ? extends ItemStack> allBanners = inventory.all(Material.BANNER);
			for(ItemStack banner : allBanners.values()) {
				Location location = event.getEntity().getLocation();
				location.getWorld().dropItemNaturally(location, banner);
			}
			inventory.remove(Material.BANNER); //specific banner?
		}
	}
}
