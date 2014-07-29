package com.qihoo.zxc.service;

import java.util.Date;
import java.util.List;

import com.qihoo.zxc.R;
import com.qihoo.zxc.activity.MainActivity;
import com.qihoo.zxc.activity.SettingActivity;
import com.qihoo.zxc.constant.Constant;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.constant.Constant.VIEW_TYPE;
import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.utils.HttpClientUtil;
import com.qihoo.zxc.utils.HttpClientUtil.RETURN_TYPE;
import com.qihoo.zxc.utils.ProcessUtil;
import com.qihoo.zxc.utils.Toast;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

class SingleThread implements Runnable {
	final String TAG = "SingleThread";
	Context context;
	Handler handler;
	
	public SingleThread(Context context) {
		this.context = context;
		this.handler = ((FloatWindowService) context).handler;
	}

	Thread thread = null;
	volatile boolean stopped;

	void start() {
		stopped = false;
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		FloatWindowService.lastGame = null;
		handler.obtainMessage(Constant.REMOVE_BIG_WINDOW).sendToTarget();
		int taskId = -1;
		while (!stopped && !Thread.interrupted()) {
			try {
//				if(!((FloatWindowService) context).screenOn)
//				synchronized (context) {
//					Log.d(TAG, "wait");
//					ActivityManager manager = (ActivityManager) context
//							.getSystemService(Context.ACTIVITY_SERVICE);
//					List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
//					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
//					ComponentName topActivity = runningTaskInfo.topActivity;
//					String currentPackageName = topActivity.getPackageName();
//					if (runningTaskInfo.id == taskId) {
//						currentPackageName = FloatWindowService.lastGame;
//					}
//					if (FloatWindowService.startTime > 0 && FloatWindowService.stopTime < 0) {
//						FloatWindowService.stopTime =  SystemClock.elapsedRealtime();
//						Log.e(TAG, currentPackageName);
//					}
//					context.wait();
//					Log.d(TAG, "continue");
//				}
				// 检测当前Activity
				ActivityManager manager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
				RunningTaskInfo runningTaskInfo = runningTasks.get(0);
				ComponentName topActivity = runningTaskInfo.topActivity;
				String currentPackageName = topActivity.getPackageName();
				if (runningTaskInfo.id == taskId) {
					currentPackageName = FloatWindowService.lastGame;
				}
				// Log.d(TAG, currentPackageName);
				long tmp = SystemClock.elapsedRealtime();
				long begin = new Date().getTime();
				if (isGame(currentPackageName)) {
					// TODO 显示悬浮框
					taskId = runningTaskInfo.id;
					if (null == MyWindowManager.floatView
							|| (null != MyWindowManager.floatView && (MyWindowManager.floatView
									.getVisibility() == View.GONE
									|| MyWindowManager.floatView.type == VIEW_TYPE.PAUSE || !FloatWindowService.lastGame
										.equals(currentPackageName)))) {
						// 刚返回/进入游戏
						// 判断time与stoptime
						do {
							if (FloatWindowService.isTimeReset()) {
								// 新开游戏
								Log.d(TAG, "新开游戏");
								FloatWindowService.startTime = tmp;
								FloatWindowService.begin = begin;
								FloatWindowService.stopTime = -1;
								
								break;
							}
							if (!FloatWindowService.lastGame
									.equals(currentPackageName)) {
								// 仍是新开游戏，但之前游戏要统计
								Log.d(TAG, "游戏跳游戏");
								saveGameTime(FloatWindowService.lastGame,
										FloatWindowService.begin, tmp
												- FloatWindowService.startTime);
								FloatWindowService.resetTime();
								FloatWindowService.startTime = tmp;
								FloatWindowService.begin = begin;
								FloatWindowService.stopTime = -1;
								break;
							}
							Log.e(TAG, "interval:"
									+ (tmp - FloatWindowService.stopTime)
									+ "ms");
							if (Constant.MIN_INTERVAL_TIME < tmp
									- FloatWindowService.stopTime) {
								// 虽然前后都是同一游戏，但相隔太久，前面算作一次游戏
								Log.d(TAG, "暂停太久，游戏");
								saveGameTime(currentPackageName,
										FloatWindowService.begin,
										FloatWindowService.stopTime
												- FloatWindowService.startTime);
								FloatWindowService.resetTime();
								FloatWindowService.startTime = tmp;
								FloatWindowService.begin = begin;
							}
							FloatWindowService.stopTime = -1;
						} while (false);
						FloatWindowService.lastGame = currentPackageName;

						ProcessUtil.clearBackgroundProcess(context, handler);

					}
					// if (0 < FloatWindowService.startTime) {
					// FloatWindowService.stopTime = tmp;
					// }
					int isHidden = LittleBoyApplication.getInstance().gamesHiddenMap
							.get(currentPackageName).hidden;
					if (0 < isHidden) {
						// Log.d(TAG, "隐藏");
						handler.obtainMessage(Constant.HIDE_MENU)
								.sendToTarget();
					} else {
						// Log.d(TAG, "显示");
						handler.obtainMessage(Constant.CREATE_BIG_WINDOW)
								.sendToTarget();
					}
					if(!((FloatWindowService) context).screenOn)
						synchronized (context) {
							Log.d(TAG, "wait is game");
							if (FloatWindowService.startTime > 0 && FloatWindowService.stopTime < 0) {
								FloatWindowService.stopTime =  SystemClock.elapsedRealtime();
								handler.obtainMessage(Constant.PAUSE_BIG_WINDOW)
								.sendToTarget();
								Log.e(TAG, "synchronized:FloatWindowService.stopTime:"+FloatWindowService.stopTime);
							}
							context.wait();
							Log.d(TAG, "continue is game");
						}
				} else {
					// 当前不是游戏
					// 判断如果刚才的游戏还在运行时，就显示返回悬浮框，否则隐藏
					taskId = -1;
					if (isTaskRunning(FloatWindowService.lastGame)) {
						// 计下末尾时间，但不统计
						if (FloatWindowService.stopTime < 0) {
							FloatWindowService.stopTime = tmp;
							Log.e(TAG, currentPackageName);
						}
						handler.obtainMessage(Constant.PAUSE_BIG_WINDOW)
								.sendToTarget();
					} else {
						// game over，计下末尾时间，统计
						if (FloatWindowService.lastGame != null
								&& !FloatWindowService.isTimeReset()) {
							saveGameTime(FloatWindowService.lastGame,
									FloatWindowService.begin, tmp
											- FloatWindowService.startTime);
						}
						FloatWindowService.resetTime();
						FloatWindowService.lastGame = null;
						handler.obtainMessage(Constant.HIDE_BIG_WINDOW)
								.sendToTarget();
					}
					if(!((FloatWindowService) context).screenOn)
						synchronized (context) {
							Log.d(TAG, "wait not game");
							context.wait();
							Log.d(TAG, "continue not game");
						}
				}
				if (FloatWindowService.alphaTime == Constant.MAX_ALPHA_TIME) {
					handler.obtainMessage(Constant.FULL_ALPHA_WINDOW)
							.sendToTarget();
				}
				if (FloatWindowService.alphaTime >= 0) {
					FloatWindowService.alphaTime--;
				}
				if (FloatWindowService.alphaTime == 0) {
					handler.obtainMessage(Constant.HALF_ALPHA_WINDOW)
							.sendToTarget();
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				thread.interrupt();
				e.printStackTrace();
			}
		}
		handler.obtainMessage(Constant.REMOVE_BIG_WINDOW).sendToTarget();
		((FloatWindowService) context).stopSelf();
	}

	void stop() {
		stopped = true;
	}

	boolean isGame(String packageName) {
		// Log.d(TAG, packageName);
		outer: do {
			PackageHidden packageHidden = null;
			if (null != (packageHidden = LittleBoyApplication.getInstance().gamesHiddenMap
					.get(packageName))) {
				return packageHidden.game > 0 ? true : false;
			}
			HttpClientUtil.RETURN_TYPE returnType = HttpClientUtil
					.checkGame(packageName);
			if(returnType == RETURN_TYPE.NORMAL && LittleBoyApplication.getInstance().already)
				try {
					LittleBoyApplication.getInstance().packageInfoList.add(context.getPackageManager().getPackageInfo(packageName, 0));
					LittleBoyApplication.getInstance().notifyDataSetChanged();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			switch (returnType) {
			case NORMAL:
				break outer;
			default:// 失败
				break;
			}
		} while (false);
		return false;
	}

	private void saveGameTime(final String pck, final long begin,
			final long duration) {
		LittleBoyApplication.getInstance().myHelper.saveGameTime(pck, begin,
				duration);
		Log.e(TAG, pck + "," + new Date(begin) + "," + (duration / 1000) + "s");
		handler.post(new Runnable() {
			@Override
			public void run() {
				if((duration / 1000) > 60)
					Toast.showLong(
							context, "您刚才玩了" + (duration / 1000 / 60) + "分钟游戏^o^");
				else
					Toast.showLong(
							context, "您刚才游戏不到1分钟ToT");
			}
		});
	}

	private boolean isRunning(String pck) {
		if (null == pck) {
			return false;
		}
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = activityManager
				.getRunningAppProcesses();
		final ActivityManager.MemoryInfo memoryBefore = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryBefore);
		for (RunningAppProcessInfo info : runningApps) {
			if (info.processName.equals(pck)) {
				return true;
			}
		}
		return false;
	}

	private boolean isTaskRunning(String pck) {
		if (null == pck) {
			return false;
		}
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(100);// Return
																			// a
																			// list
																			// of
																			// the
																			// tasks
		for (RunningTaskInfo runningTaskInfo : runningTasks) {
			if (runningTaskInfo.topActivity.getPackageName().equals(pck)) {
				return true;
			}
		}

		return false;
	}

}

public class FloatWindowService extends Service {
	public boolean screenOn = true;
	public static int alphaTime = Constant.MAX_ALPHA_TIME;

	public static void resetAlphaTime() {
		MyWindowManager.fullAlphaMenu();
		alphaTime = Constant.MAX_ALPHA_TIME;
	}

	public static String lastGame = null;
	public static long begin;
	public static long startTime;
	public static long stopTime;

	public static void resetTime() {
		startTime = stopTime = begin = -1;
	}

	public static boolean isTimeReset() {
		if (begin > 0) {
			return false;
		}
		if (startTime > 0) {
			return false;
		}
		if (stopTime > 0) {
			return false;
		}
		return true;
	}

	String TAG = "FloatWindowService";

	SingleThread thread;

	String lastGamePackageName = null;

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.CREATE_BIG_WINDOW:
				MyWindowManager.createFloatView(FloatWindowService.this);
				break;
			case Constant.REMOVE_BIG_WINDOW:
				MyWindowManager.removeFloatView(FloatWindowService.this);
				break;
			case Constant.HIDE_BIG_WINDOW:
				MyWindowManager.hideFloatView(FloatWindowService.this);
				break;
			case Constant.PAUSE_BIG_WINDOW:
				MyWindowManager.pauseFloatView();
				break;
			case Constant.HIDE_MENU:
				MyWindowManager.hideMenu(FloatWindowService.this);
				break;
			case Constant.FULL_ALPHA_WINDOW:
				MyWindowManager.fullAlphaMenu();
				break;
			case Constant.HALF_ALPHA_WINDOW:
				MyWindowManager.halfAlphaMenu();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public Intent mainActivityIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		setFrontService();
		thread = new SingleThread(this);
		thread.start();
		this.registerReceiver(screenOffReceiver, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));
		this.registerReceiver(screenOnReceiver, new IntentFilter(
				"android.intent.action.SCREEN_ON"));
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		this.unregisterReceiver(screenOnReceiver);
		this.unregisterReceiver(screenOffReceiver);
		thread.stop();
		super.onDestroy();
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	private void setFrontService() {
		Notification notification = new Notification(R.drawable.ic_launcher,
				getResources().getString(R.string.notification_notify),
				System.currentTimeMillis());
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this,
				getResources().getString(R.string.notification_title),
				getResources().getString(R.string.notification_desc),
				pendingIntent);
		startForeground(9527, notification);
	}
	
	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "off");
			synchronized (FloatWindowService.this) {
				screenOn = false;
				
			}
		}

	};
	private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "on");
			synchronized (FloatWindowService.this) {
				screenOn = true;
				FloatWindowService.this.notify();
			}
		}

	};
}