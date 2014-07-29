package com.qihoo.zxc.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.qihoo.zxc.R;
import com.qihoo.zxc.constant.Constant.VIEW_TYPE;
import com.qihoo.zxc.view.FloatView;
import com.qihoo.zxc.view.StrategyView;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class MyWindowManager {
	static String TAG = "MyWindowManager";
	private final static int FLAG_APKTOOL_VALUE = 1280;
	public static FloatView floatView;
	public static StrategyView strategyView;
	public static android.view.WindowManager.LayoutParams floatViewParams;
	public static android.view.WindowManager.LayoutParams strategyViewParams;
	private static WindowManager mWindowManager;

	private static ActivityManager mActivityManager;

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	public static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}
	static int animation = 0;
	public static void animate() {
		if (animation == 1) {
			Log.d(TAG, "animate");
			floatView.animateGift(true);
		}else if (animation == 4){
			Log.d(TAG, "animate");
			floatView.animateGift(false);
		}
		animation = (animation + 1)%6;
	}
	public static void createFloatView(Context context) {
		if (floatView == null) {
			WindowManager windowManager = getWindowManager(context);
			Log.d(TAG, "createFloatView");
			floatView = new FloatView(context);
			if (floatViewParams == null) {
				floatViewParams = new LayoutParams();
				floatViewParams.type = LayoutParams.TYPE_PHONE;
				floatViewParams.flags/* FLAG_NOT_TOUCH_MODAL */|= LayoutParams.FLAG_NOT_FOCUSABLE;
				floatViewParams.format = PixelFormat.RGBA_8888;
				floatViewParams.width = LayoutParams.WRAP_CONTENT;
				floatViewParams.height = LayoutParams.WRAP_CONTENT;
				floatViewParams.gravity = Gravity.LEFT | Gravity.TOP;
				Point point = new Point();
				windowManager.getDefaultDisplay().getSize(point);
				floatViewParams.x = 0;
				floatViewParams.y = point.y/2;
//				floatViewParams.flags |= FLAG_APKTOOL_VALUE;
				Log.d(TAG, floatViewParams.x + "," + floatViewParams.y);
			}
			windowManager.addView(floatView, floatViewParams);
			FloatWindowService.resetAlphaTime();
		}
		animate();
		floatView.inGame();
		floatView.hideGift();
		apparentStrategyView();
		floatView.setVisibility(View.VISIBLE);
	}

	public static void createStrategyView(Context context,String strategy) {
		if (strategyView == null) {
			final WindowManager windowManager = getWindowManager(context);
			Log.d(TAG, "createStrategyView");
			strategyView = new StrategyView(context);
			if (strategyViewParams == null) {
				strategyViewParams = new LayoutParams();
				strategyViewParams.type = LayoutParams.TYPE_PHONE;
				strategyViewParams.format = PixelFormat.RGBA_8888;
				strategyViewParams.width = LayoutParams.WRAP_CONTENT;
				strategyViewParams.height = LayoutParams.WRAP_CONTENT;
				strategyViewParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
//				Point point = new Point();
//				windowManager.getDefaultDisplay().getSize(point);
//				strategyViewParams.x = point.x/2 - 150;
//				strategyViewParams.y = point.y;
				strategyViewParams.flags/* FLAG_NOT_TOUCH_MODAL */|= LayoutParams.FLAG_NOT_FOCUSABLE;
				// bigWindowParams.flags = FLAG_APKTOOL_VALUE;
			}
			windowManager.addView(strategyView, strategyViewParams);
			FloatWindowService.resetAlphaTime();
		}
		strategyView.setVisibility(View.VISIBLE);
	}
	
	public static void removeFloatView(Context context) {
		removeStrategyView(context);
		if (null != floatView) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(floatView);
			floatView = null;
		}
	}
	public static void removeStrategyView(Context context) {
		if (null != strategyView) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(strategyView);
			strategyView = null;
		}
	}

	public static void hideFloatView(Context context) {
		removeStrategyView(context);
		if (null != floatView) {
			floatView.apparentMenu();
			floatView.inGame();
			floatView.hideExpandViews();
			floatView.setVisibility(View.GONE);
		}
	}
	public static void hideStrategyView() {
		if (null != strategyView) {
			strategyView.setVisibility(View.GONE);
		}
	}
	public static void apparentStrategyView() {
		if (null != strategyView) {
			strategyView.setVisibility(View.VISIBLE);
		}
	}
	public static void pauseFloatView() {
		if (null != floatView) {
			hideStrategyView();
			floatView.apparentMenu();
			floatView.hideExpandViews();
			floatView.setVisibility(View.VISIBLE);
			floatView.pause();
		}
	}

	public static void hideMenu(Context context) {
		createFloatView(context);
		floatView.hideMenu();
		floatView.setVisibility(View.VISIBLE);
	}

	public static void fullAlphaMenu() {
		if (null != floatView) {
			floatView.fullAlphaMenu();
		}
	}
	public static void halfAlphaMenu() {
		if (null != floatView) {
			floatView.halfAlphaMenu();
		}
	}
	
	public static void screenshot() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Display display = mWindowManager.getDefaultDisplay();
		Matrix displayMetrix = new Matrix();  
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getRealMetrics(displayMetrics);
		float[] dims = {displayMetrics.widthPixels, displayMetrics.heightPixels};  
		float degrees = getDegreesForRotation(display.getRotation());  
        boolean requiresRotation = (degrees > 0);  
        if (requiresRotation) {  
            // Get the dimensions of the device in its native orientation  
        	displayMetrix.reset();  
        	displayMetrix.preRotate(-degrees);  
        	displayMetrix.mapPoints(dims);  
            dims[0] = Math.abs(dims[0]);  
            dims[1] = Math.abs(dims[1]);  
        }
        Class sf = Class.forName("android.view.Surface");
        Method method = sf.getMethod("screenshot", new Class[]{int.class,int.class});
        Bitmap mScreenBitmap = (Bitmap) method.invoke(null, new Object[]{ (int) dims[0], (int) dims[1]});
        if (requiresRotation) {  
            // Rotate the screenshot to the current orientation  
            Bitmap ss = Bitmap.createBitmap(displayMetrics.widthPixels,  
            		displayMetrics.heightPixels, Bitmap.Config.ARGB_8888);  
            Canvas c = new Canvas(ss);  
            c.translate(ss.getWidth() / 2, ss.getHeight() / 2);  
            c.rotate(degrees);  
            c.translate(-dims[0] / 2, -dims[1] / 2);  
            c.drawBitmap(mScreenBitmap, 0, 0, null);  
            c.setBitmap(null);  
            mScreenBitmap = ss;  
        }  

        // If we couldn't take the screenshot, notify the user  
        if (mScreenBitmap == null) {  
             
            return;  
        }  
        // Optimizations  
        mScreenBitmap.setHasAlpha(false);  
        mScreenBitmap.prepareToDraw();  
	}
	
	static private float getDegreesForRotation(int value) {  
        switch (value) {  
        case Surface.ROTATION_90:  
            return 360f - 90f;  
        case Surface.ROTATION_180:  
            return 360f - 180f;  
        case Surface.ROTATION_270:  
            return 360f - 270f;  
        }  
        return 0f;  
    }
}