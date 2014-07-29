package com.qihoo.zxc.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.utils.MySQLiteOpenHelper;
import com.qihoo.zxc.utils.ProcessUtil;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.BaseAdapter;

public class LittleBoyApplication extends Application{
	final String TAG = "LittleBoyApplication";
	
	public HashMap<String, PackageHidden> gamesHiddenMap;
	
	public MySQLiteOpenHelper myHelper;
	
	static LittleBoyApplication instance;
	
	public static LittleBoyApplication getInstance(){
		return instance;
	}
	
	public boolean isFloatWindowsServiceStatus = true;
	
	public boolean already = false;
	
	List<BaseAdapter> registeredAdapters = new ArrayList<BaseAdapter>();
	public List<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();
	
	synchronized public void registerAdapter(BaseAdapter adapter) {
		registeredAdapters.add(adapter);
	}
	
	synchronized public void unRegisterAdapter(BaseAdapter adapter) {
		for (BaseAdapter v : registeredAdapters) {
			if(v == adapter)
				registeredAdapters.remove(v);
		}
	}
	
	public void notifyDataSetChanged(){
		for (BaseAdapter adapter : registeredAdapters) {
			adapter.notifyDataSetChanged();
			Log.e(TAG, "notifyDataSetChanged:"+adapter.toString());
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		gamesHiddenMap = new HashMap<String, PackageHidden>();
		instance = this;
		myHelper = new MySQLiteOpenHelper(this, Constant.DB_NAME, null, 5);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "SELECT * FROM hide_apps");
				SQLiteDatabase db = myHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery("SELECT * FROM hide_apps", null);
				if(cursor != null)
					while (cursor.moveToNext()) {
						String packageName = cursor.getString(cursor
								.getColumnIndex(Constant.DB_COLUMN_PACKAGENAME));
						int isHidden = cursor.getInt(cursor
								.getColumnIndex(Constant.DB_COLUMN_HIDDEN));
						String strategy = cursor.getString(cursor
								.getColumnIndex(Constant.DB_COLUMN_STRATEGY));
						int isGame = cursor.getInt(cursor
								.getColumnIndex(Constant.DB_COLUMN_GAME));
						int hasGift = cursor.getInt(cursor
								.getColumnIndex(Constant.DB_COLUMN_GIFT));
						gamesHiddenMap.put(packageName, new PackageHidden(strategy, isHidden, isGame,hasGift,packageName));
					}
				if(cursor != null)
					cursor.close();
				db.close();
				ProcessUtil.firstFilterAllGameApps(instance, packageInfoList, gamesHiddenMap);
				System.gc();
				already = true;
			}
		}).start();
		
	}
	
	public void setFloatWindowsServiceStatusOpen() {
		isFloatWindowsServiceStatus = true;
	}
	
	public void setFloatWindowsServiceStatusClose() {
		isFloatWindowsServiceStatus = false;
	}
	
	public boolean getFloatWindowsServiceStatus() {
		return isFloatWindowsServiceStatus;
	}
	
}
