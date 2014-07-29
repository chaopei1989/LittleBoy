package com.qihoo.zxc.constant;

import java.util.HashSet;

public class Constant {
	
	public static final int CREATE_BIG_WINDOW = 7190022;
	public static final int REMOVE_BIG_WINDOW = 7190104;
	public static final int HIDE_BIG_WINDOW = 7192257;
	public static final int PAUSE_BIG_WINDOW = 7192339;

	public static final String DB_TABLE_HIDE_APPS = "hide_apps";
	public static final String DB_COLUMN_PACKAGENAME = "DB_COLUMN_PACKAGENAME";
	public static final String DB_COLUMN_HIDDEN = "DB_COLUMN_HIDDEN";
	public static final String DB_COLUMN_STRATEGY = "DB_COLUMN_STRATEGY";
	public static final String DB_COLUMN_GAME = "DB_COLUMN_GAME";
	public static final String DB_COLUMN_GIFT = "DB_COLUMN_GIFT";
	
	public static final String DB_TABLE_APP_DURATION = "app_duration";
	public static final String DB_COLUMN_ID = "DB_COLUMN_ID";
	public static final String DB_COLUMN_DURATION = "DB_COLUMN_DURATION";
	public static final String DB_COLUMN_BEGIN = "DB_COLUMN_BEGIN";
	
	public static final int HIDE_MENU = 7202228;
	public static final int MAX_ALPHA_TIME = 6;
	public static final int FULL_ALPHA_WINDOW = 7211710;
	public static final int HALF_ALPHA_WINDOW = 7211711;
	public static final long MIN_INTERVAL_TIME = 120000;
	public static final float FULL_ALPHA = 1.0f;
	public static final float HALF_ALPHA = 0.5f;
	
	static String APP_NAME = "LittleBoy";
	static String DB_NAME = "littleboy.db";
	public static HashSet<String> filterPackage = new HashSet<String>();
	static{
		filterPackage.add("com.supercell.clashofclans");
		filterPackage.add("cat.game");
		filterPackage.add("com.qihoo.expressbrowser");
		filterPackage.add("com.hg.ninjaherocatsfree");
		filterPackage.add("cn.ibuka.manga.ui");
		filterPackage.add("com.veewo.a1024");
	}
	
	public enum VIEW_TYPE{
		PAUSE,GAME
	}
	public enum VIEW_POSITION{
		LEFT,RIGHT,TOP,BOTTOM
	}
	public static String SHORT_CUT_TITLE = "°²È«ÓÎÏ·ºÐ";
	public static String SHORT_CUT_ACTION = "com.qihoo.zxc.shortcut";
}
