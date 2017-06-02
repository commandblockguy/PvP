package com.commandblockguy.arena;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

public class Round {

	public String name = "Round";
	public int delay = 10;
	public int length;
	public boolean timeLimit = false;
	public ArrayList<Team> teams;
	public int remainingSeconds;
	public boolean started = false;
	public boolean delayCounting = false;
	public boolean ended = false;
	public PVPEvent event;
	
	public Round(PVPEvent event, String name) {
		 this.name = name;
		 this.event = event;
	}
	
	public boolean inProgress() {
		return started && !ended; 
	}
	public void startImmediately() {
		delayCounting = false;
		started = true;
		remainingSeconds = length;
		event.messagePlayers(name + " starting!");
		//TODO: Register listeners that handle stats
		if(timeLimit) {
			new BukkitRunnable() {public void run() {end();}}.runTaskLater(Main.plugin, length * 20);
		}
	}
	public void startWithDelay(int delay) {
		this.delay = delay;
		delayCounting = true;
		event.messagePlayers(name + " starting in " + this.delay + " seconds!");
		new BukkitRunnable() {public void run() {startImmediately();}}.runTaskLater(Main.plugin, delay * 20);
	}
	public void startWithDelay(int delay, int length) {
		startWithDelay(delay);
		this.length = length;
		timeLimit = true;
	}
	public void startWithDelay() {
		startWithDelay(delay);
	}

	public void end() {
		ended = true;
		event.messagePlayers(name + " has ended!");
		event.onRoundEnd();
	}
}
