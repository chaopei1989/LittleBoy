package com.qihoo.zxc.utils;

import com.qihoo.zxc.R;
import com.qihoo.zxc.activity.SettingActivity;
import com.qihoo.zxc.activity.ShortCutActivity;
import com.qihoo.zxc.constant.Constant;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;

public class ShortCutUtil {
	
	public static void searchInAppstore(String str,Context context) {
		Intent intent = context.getPackageManager()
				.getLaunchIntentForPackage("com.qihoo.appstore");
		intent.putExtra("from_out_side","feiyang");
		intent.putExtra("start_activity_index",26);
		intent.putExtra("SearchWord",str);
		context.startActivity(intent);
	}
	
	public static void createShortCut(Context context) {
		if(isShortCutInstalled(context)){
			Toast.showShort(context, "\"安全游戏盒\"已存在");
			return;
		}
		Intent sIntent = new Intent(Constant.SHORT_CUT_ACTION);
		sIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		Intent installer = new Intent("com.android.launcher.action.INSTALL_SHORTCUT"); 
		installer.putExtra("duplicate", false); 
		installer.putExtra(Intent.EXTRA_SHORTCUT_NAME, Constant.SHORT_CUT_TITLE); 
		installer.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.shortcut_icon)); 
		installer.putExtra(Intent.EXTRA_SHORTCUT_INTENT, sIntent); 
		context.sendBroadcast(installer);
		Toast.showShort(context, "\"安全游戏盒\"创建成功");
	}
	
	public static boolean isShortCutInstalled(Context context) {
		boolean isInstallShortcut = false;  
        final ContentResolver cr = context.getContentResolver();  
        String AUTHORITY;
        if (8 < android.os.Build.VERSION.SDK_INT) {
        	AUTHORITY = "com.android.launcher2.settings";  
		}else {
			AUTHORITY = "com.android.launcher.settings";  
		}
        final Uri CONTENT_URI = Uri.parse("content://" +AUTHORITY + "/favorites?notify=true");  
        Cursor c = cr.query(CONTENT_URI,new String[] {"title","iconResource" },"title=?",  
        new String[] {Constant.SHORT_CUT_TITLE}, null);  
        if(c!=null && c.getCount()>0){  
            isInstallShortcut = true ;  
        }
        if(c!=null)
        	c.close();
        return isInstallShortcut ;  
	}
	
	public static void deleteShorCut(Context context) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT"); 
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,Constant.SHORT_CUT_TITLE); 
		Intent intent = new Intent(Constant.SHORT_CUT_ACTION);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent); 
		context.sendBroadcast(shortcut); 
	}
	
}
