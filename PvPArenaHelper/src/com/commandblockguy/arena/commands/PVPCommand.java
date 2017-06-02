package com.commandblockguy.arena.commands;

import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.commandblockguy.arena.EventType;
import com.commandblockguy.arena.Main;
import com.commandblockguy.arena.PVPEvent;
import com.commandblockguy.arena.PVPPlayer;
import com.commandblockguy.arena.Team;
import com.commandblockguy.arena.TeamColor;
import com.commandblockguy.arena.TeamSelection;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class PVPCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("Usage: /pvp [register|event|join|leave|base]");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only a player can run this command!");
			return false;
		}
		registerPlayer((Player) sender);
		PVPPlayer player = Main.getPlayer((Player) sender);
		switch(args[0]) {
			default:
				sender.sendMessage("Usage: /pvp [register|join|leave|base|team|event|round]");
				break;
			case "register":
				register(player, args);
				break;
			case "event":
				event(player, args);
				break;
			case "join":
				join(player, args);
				break;
			case "leave":
				leave(player, args);
				break;
			case "base":
				base(player, args);
				break;
			case "round":
				round(player, args);
				break;
			case "team":
				team(player, args);
		}
		return true;
	}

	public void register(PVPPlayer player, String[] args) {
		PVPEvent event;
		try {
			Town town = player.getResident().getTown();
			event = new PVPEvent(town);
			event.setAdmin(player);
			Main.setEvent(event);
			Bukkit.broadcastMessage("A PVP event has been created in " + town + "! To join, type /pvp join " + town);
			player.setEvent(Main.getEvent(town.getName()));
		} catch (NotRegisteredException e) {
			player.getPlayer().sendMessage("You do not belong to a town");
			return;
		}
	}
	public void event(PVPPlayer player, String[] args) {
		if(player.getEvent() == null) {
			player.getPlayer().sendMessage("You are not participating in an event!");
			return;
		}
		if(!player.getEvent().getAdmin().equals(player)) {
			player.getPlayer().sendMessage("You are not the event admin!");
			return;
		}
		if(args.length == 1) {
			player.getPlayer().sendMessage("Usage: /pvp event [type|teams|end]");
			return;
		}
		switch(args[1]) {
			default:
				player.getPlayer().sendMessage("Usage: /pvp event [type|teams|chest|end]");
				break;
			case "teams":
				teamType(player, args);
				break;
			case "type":
				eventType(player, args);
				break;
			case "chest":
				if(args.length == 2) {
					player.getPlayer().sendMessage("Usage: /pvp event chest [team]");
				} else {
					Team team;
					if(args[2].equalsIgnoreCase("red")) {
						team = player.getEvent().getRedTeam();
					} else if(args[2].equalsIgnoreCase("blue")) {
						team = player.getEvent().getBlueTeam();
					} else {
						player.getPlayer().sendMessage(args[2] + " is not a valid team!");
						break;
					}
					Block block = player.getPlayer().getTargetBlock((Set<Material>) null, 64);
					if(block.getType().equals(Material.CHEST)) {
						team.setChest(block.getLocation());
						player.getPlayer().sendMessage("Chest set.");
					} else {
						player.getPlayer().sendMessage("Does that look like a chest to you? lol");
					}
				}
				break;
			case "end":
				String town = player.getEvent().getTown().getName();
				Bukkit.broadcastMessage("The PVP event in " + town + " has ended!");
				Main.endEvent(town);
				break;
		}
	}

	public void teamType(PVPPlayer player, String[] args) {
		if(args.length == 2) {
			player.getPlayer().sendMessage("Usage: /pvp event teams [select|random|rerandom|off]");
			return;
		}
		if(args[2].equalsIgnoreCase("shuffle")) {
			player.getEvent().randomizeTeams();
			player.getEvent().messagePlayers("Teams have been randomized!");
			return;
		}
		try {
			player.getEvent().setSelectionType(TeamSelection.valueOf(args[2].toUpperCase()));
			player.getEvent().messagePlayers("Team selection has been set to " + args[2].toUpperCase());
			if(args[2].toLowerCase().equals("random") || args[2].toLowerCase().equals("rerandom")) {
				player.getEvent().randomizeTeams();
			}
		} catch(IllegalArgumentException e) {
			player.getPlayer().sendMessage("Usage: /pvp event teams [select|random|rerandom|off]");
		}
	}
	public void eventType(PVPPlayer player, String[] args) {
		if(args.length == 2) {
			player.getPlayer().sendMessage("Event type: " + player.getEvent().getEventType());
			return;
		}
		try {
			player.getEvent().setEventType(EventType.valueOf(args[2].toUpperCase()));
			player.getEvent().messagePlayers("Event type has been set to " + args[2].toUpperCase());
		} catch(IllegalArgumentException e) {
			player.getPlayer().sendMessage("Usage: /pvp event type [ctf|tdm|ffa|tffa|other]");
		}
	}

	public void join(PVPPlayer player, String[] args) {
		if(args.length != 2) {
			try {
				String townName = WorldCoord.parseWorldCoord(player.getPlayer()).getTownBlock().getTown().getName();
				player.setEvent(Main.getEvent(townName)); //default to standing town
				player.getPlayer().sendMessage("Joining event in " + townName);
			} catch (NotRegisteredException e) {
				player.getPlayer().sendMessage("Usage: /pvp join (town)");
			} catch (NullPointerException e) {
				player.getPlayer().sendMessage("Usage: /pvp join (town)");
			}
			return;
		}
		if(!TownyUniverse.getDataSource().hasTown(args[1])) { //Make sure it's a valid town
			player.getPlayer().sendMessage(args[1] + " is not a valid town name.");
			return;
		}
		if(!Main.eventMap.containsKey(args[1].toLowerCase())) {
			player.getPlayer().sendMessage(args[1] + " is not holding an event right now.");
			return;
		}
		player.leaveEvent();
		if(args.length == 2) {
			player.setEvent(Main.getEvent(args[1]));
		}
	}
	public void leave(PVPPlayer player, String[] args) {
		player.leaveEvent();
		player.getPlayer().sendMessage("Leaving PvP event!");
	}
	public void base(PVPPlayer player, String[] args) {
		if(args.length == 1) {
			player.tpToBase();
			return;
		}
		if(args[1].equals("set") && player.participating()) { 
				player.setBaseHere();
				player.getPlayer().sendMessage("Setting base location");
		} else {
				player.getPlayer().sendMessage("You are not in the arena area!");
		}
	}
	private void round(PVPPlayer player, String[] args) {
		if(player.getEvent() == null) {
			player.getPlayer().sendMessage("You are not participating in an event!");
			return;
		}
		if(!player.getEvent().getAdmin().equals(player)) {
			player.getPlayer().sendMessage("You are not the event admin!");
			return;
		}
		if(args.length == 1) {
			player.getPlayer().sendMessage("Usage: /pvp round [add|start|end|options]");
			return;
		}
		PVPEvent event = player.getEvent();
		switch(args[1]) {
		default:
			player.getPlayer().sendMessage("Usage: /pvp round [add|start|end|options]");
			break;
		case "add":
			if(args.length == 2) {
				player.getPlayer().sendMessage("Usage: /pvp round add (name)");
				return;
			}
			if(player.getEvent().getEventType().equals(EventType.CTF) && !player.getEvent().chestsSet()) {
				player.getPlayer().sendMessage("Please specify a base (single) chest for each team");
				return;
			}
			String[] clone = Arrays.copyOfRange(args, 2, args.length);
			String name = String.join(" ", clone);
			event.addRound(name);
			player.getPlayer().sendMessage("Successfully added round: " + name);
			break;
		case "start":
			if(!event.allHaveTeam() && event.getSeletectionType() == TeamSelection.SELECT && event.getEventType().usesTeams()) {
				player.getPlayer().sendMessage("Some players have not set teams!");
				return;
			}
			if(args.length == 2) {
				event.startRound();
			} else {
				if(args.length == 3)
					try {
						int delay = Integer.valueOf(args[2]);
						event.startRound(delay);
					}
					catch(Exception e) {
						player.getPlayer().sendMessage("Usage: /pvp round start [length] [delay]");
						e.printStackTrace();
					}
				if(args.length == 4)
					try {
						int delay = Integer.valueOf(args[2]);
						int length = Integer.valueOf(args[3]);
						event.startRound(delay, length);
					}
					catch(Exception e) {
						player.getPlayer().sendMessage("Usage: /pvp round start [length] [delay]");
						e.printStackTrace();
					}
			}
			break;
		case "end":
			event.endRound();
		}
	}
	public void team(PVPPlayer player, String[] args) {
		if(args.length == 1) {
			switch(player.getTeamColor()) {
				default:
					player.getPlayer().sendMessage("You are not on a team");
					break;
				case RED:
					player.getPlayer().sendMessage("You are on the " + ChatColor.RED + "RED Team");
					break;
				case BLUE:
					player.getPlayer().sendMessage("You are on the " + ChatColor.BLUE + "BLUE Team");
					break;
			}
			return;
		}
		if(args[1].equalsIgnoreCase("list")) {
			player.getPlayer().sendMessage(ChatColor.RED + "RED Team:");
			String playerlist = "Players: ";
			for(PVPPlayer p : player.getEvent().getRedTeam().getPlayers()) {
				playerlist += p.getPlayer().getName();
			}
			player.getPlayer().sendMessage(playerlist);
			player.getPlayer().sendMessage(ChatColor.BLUE + "BLUE Team:");
			playerlist = "Players: ";
			for(PVPPlayer p : player.getEvent().getBlueTeam().getPlayers()) {
				playerlist += p.getPlayer().getName();
			}
			player.getPlayer().sendMessage(playerlist);
		}
		if(player.getEvent().getSeletectionType().equals(TeamSelection.SELECT)) {
			try {
				TeamColor color = TeamColor.valueOf(args[1].toUpperCase());
				player.getEvent().setTeam(player, color);
			} catch(IllegalArgumentException exception) {
				player.getPlayer().sendMessage(args[1] + " is not a valid team color!");
			}
		}

	}
	
	public void registerPlayer(Player player) {
		if(!Main.playerMap.containsKey(player.getUniqueId())) {
			Main.setPlayer(new PVPPlayer(player));
		}
	}
	
}
