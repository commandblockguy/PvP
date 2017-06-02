package com.commandblockguy.arena.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.commandblockguy.arena.EventType;
import com.commandblockguy.arena.Main;
import com.commandblockguy.arena.PVPEvent;
import com.commandblockguy.arena.PVPPlayer;

public class InventoryListener implements Listener {
	
	@EventHandler
	public void onInventoryInteraction(InventoryClickEvent event) {
		PVPPlayer player = Main.getPlayer((Player) event.getWhoClicked());
		if(player == null) return;
		if(!player.participating()) return;
		ItemStack item;
		InventoryAction action = event.getAction();
		if(!event.getView().getTopInventory().getType().equals(InventoryType.CHEST)) return;
		if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			item = event.getCurrentItem();
		} else {
			item = event.getCursor();
			if(event.getRawSlot() > InventoryType.CHEST.getDefaultSize() - 1) return;
		}
		if(!item.getType().equals(Material.BANNER)) return;
		if(action == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
		   action == InventoryAction.PLACE_ALL ||
		   action == InventoryAction.PLACE_ONE ||
		   action == InventoryAction.PLACE_SOME) {
			
			PVPEvent pvpEvent = player.getEvent();
			if(!pvpEvent.roundInProgress()) return;
			if(!pvpEvent.getEventType().equals(EventType.CTF)) return;
			if(!event.getInventory().getLocation().equals(player.getTeam().getChest())) return;
			if(item.getDurability() == player.getTeamColor().getBannerData()) return;
			pvpEvent.messagePlayers(event.getWhoClicked().getName() + " has captured a flag!");
			pvpEvent.endRound();
		}
	}

}
