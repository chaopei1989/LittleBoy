package com.qihoo.zxc.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.utils.HttpClientUtil.RETURN_TYPE;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

public class ProcessUtil {
	final static String TAG = "ProcessUtil";
	public static boolean isServiceRunning(
			List<ActivityManager.RunningServiceInfo> serviceList,
			String... classNames) {
		if (serviceList.size() == 0) {
			return false;
		}
		for (String className : classNames) {
			for (int i = 0; i < serviceList.size(); i++) {
				if (serviceList.get(i).service.getClassName().equals(className) == true) {
					return true;
				}
			}
		}
		return false;
	}
	public static String getCurrentPackageName(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		RunningTaskInfo runningTaskInfo = runningTasks.get(0);
		ComponentName topActivity = runningTaskInfo.topActivity;
		String currentPackageName = topActivity.getPackageName();
		return currentPackageName;
	}

	public static void clearBackgroundProcess(String pck, Context context)
			throws NameNotFoundException {
		PackageInfo p = context.getPackageManager().getPackageInfo(pck, 0);
		ActivityManager a = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		a.killBackgroundProcesses(p.applicationInfo.processName);
		// Log.e(TAG, p.applicationInfo.processName);
	}

	public static Set<String> getHomes(Context context) {
		Set<String> names = new HashSet<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	public static void clearBackgroundProcess(final Context context,
			Handler handler) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = activityManager
				.getRunningAppProcesses();
		final ActivityManager.MemoryInfo memoryBefore = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryBefore);
		for (RunningAppProcessInfo info : runningApps) {
			try {
				if (!ProcessUtil.getHomes(context).contains(info.processName)
						&& !info.processName.equals("com.qihoo.appstore")) {
					ProcessUtil.clearBackgroundProcess(info.processName,
							context);
				}
			} catch (NameNotFoundException e) {
				
			}
		}
		final ActivityManager.MemoryInfo memoryAfter = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryAfter);
		handler.post(new Runnable() {
			@Override
			public void run() {
				long av = (memoryAfter.availMem - memoryBefore.availMem) * 100
						/ memoryBefore.availMem;
				if (av > 1) {
					Toast.showShort(context, "360小玩伴为您加速 " + av + "%");
				} else {
					Toast.showShort(context, "手机状态很好哦");
				}
			}
		});
	}

	public static void firstFilterAllGameApps(Context context,
			List<PackageInfo> outList/* 保证为空 */,
			Map<String, PackageHidden> outMap/* 已经读出本地的 */) {
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			// TODO 过滤包名
			String packageName = packageInfo.packageName;
			if (outMap.containsKey(packageName)) {
				if (0 < outMap.get(packageName).game) {// 是游戏
					outList.add(packageInfo);
					LittleBoyApplication.getInstance().notifyDataSetChanged();
				}
				continue;
			}
			HttpClientUtil.RETURN_TYPE returnType;
			do {
				returnType = HttpClientUtil.checkGame(packageName);
				if (returnType == RETURN_TYPE.NETWORK) {
					try {
						//Toast.showShort(context, "为了保证您安全游戏，请打开网络连接");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						break;
					}
				}
				break;
			} while (true);
			if (returnType == RETURN_TYPE.NORMAL) {
				outList.add(packageInfo);
				LittleBoyApplication.getInstance().notifyDataSetChanged();
			}
		}
	}
}
