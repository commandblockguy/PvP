package com.commandblockguy.arena;

public enum TeamColor {
	RED(1), BLUE(4), NONE(0);
	
	int bannerData;
	
	TeamColor(int bannerData) {
		this.bannerData = bannerData;
	}
	
	public int getBannerData() {
		return bannerData;
	}
}
