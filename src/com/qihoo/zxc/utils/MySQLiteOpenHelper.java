package com.qihoo.zxc.utils;

import com.qihoo.zxc.constant.Constant;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.service.FloatWindowService;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	String TAG = "MySQLiteOpenHelper";

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Log.e(TAG, "MySQLiteOpenHelper()");
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE hide_apps ( DB_COLUMN_PACKAGENAME text PRIMARY KEY , DB_COLUMN_HIDDEN integer , DB_COLUMN_GAME integer , DB_COLUMN_STRATEGY text ,DB_COLUMN_GIFT integer)");

		} catch (Exception e) {
			// db.execSQL("DROP TABLE hide_apps");
			// db.execSQL("CREATE TABLE hide_apps ( DB_COLUMN_PACKAGENAME text PRIMARY KEY , DB_COLUMN_HIDDEN integer , DB_COLUMN_GAME integer , DB_COLUMN_STRATEGY text )");
		}
		try {
			db.execSQL("CREATE TABLE app_duration ( DB_COLUMN_ID integer PRIMARY KEY, DB_COLUMN_PACKAGENAME text, DB_COLUMN_BEGIN integer, DB_COLUMN_DURATION integer )");
		} catch (Exception e) {
			// db.execSQL("DROP TABLE app_duration");
			// db.execSQL("CREATE TABLE app_duration ( DB_COLUMN_ID integer PRIMARY KEY, DB_COLUMN_PACKAGENAME text, DB_COLUMN_BEGIN integer, DB_COLUMN_DURATION integer )");
		}
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(TAG, "onUpgrade");
	}

	public void saveGameTime(String pck, long begin, long duration) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constant.DB_COLUMN_PACKAGENAME, pck);
		values.put(Constant.DB_COLUMN_BEGIN, begin);
		values.put(Constant.DB_COLUMN_DURATION, duration);
		db.insertWithOnConflict(Constant.DB_TABLE_APP_DURATION, null, values,
				SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
	}

	public void saveAppHidden(String pck, int isHidden) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constant.DB_COLUMN_HIDDEN, isHidden);
		String[] args = new String[1];
		args[0] = pck;
		db.update(Constant.DB_TABLE_HIDE_APPS, values,
				"DB_COLUMN_PACKAGENAME=?", args);
		db.close();
		LittleBoyApplication.getInstance().gamesHiddenMap
				.get(pck).hidden = isHidden;
	}

	public void saveAppHidden(String pck, int isHidden, String strategy,
			int isGame, int hasGift) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constant.DB_COLUMN_PACKAGENAME, pck);
		values.put(Constant.DB_COLUMN_HIDDEN, isHidden);
		values.put(Constant.DB_COLUMN_GAME, isGame);
		values.put(Constant.DB_COLUMN_STRATEGY, strategy);
		values.put(Constant.DB_COLUMN_GIFT, hasGift);
		db.insertWithOnConflict(Constant.DB_TABLE_HIDE_APPS, null, values,
				SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		LittleBoyApplication.getInstance().gamesHiddenMap.put(pck,
				new PackageHidden(strategy, isHidden, isGame, hasGift, pck));
	}
}
