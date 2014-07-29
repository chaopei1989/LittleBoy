package com.qihoo.zxc.model;

public class PackageHidden {
	public String strategy;
	public int hasGift;
	public int hidden;
	public int game;
	public String packageName;
	public PackageHidden(String strategy, int hidden, int game ,int hasGift,
			String packageName) {
		super();
		this.hasGift = hasGift;
		this.strategy = strategy;
		this.hidden = hidden;
		this.game = game;
		this.packageName = packageName;
	}
	
}
