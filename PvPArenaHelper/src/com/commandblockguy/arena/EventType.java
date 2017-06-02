package com.commandblockguy.arena;

public enum EventType {
	CTF(true),DM(false),TDM(true),FFA(false),TFFA(true),OTHER(false);
	
	boolean useTeams;
	
	private EventType(boolean team) {
		useTeams = team;
	}
	public boolean usesTeams() {
		return useTeams;
	}
}
