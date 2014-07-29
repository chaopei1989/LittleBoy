package com.qihoo.zxc.activity;

import java.util.List;

import com.qihoo.zxc.R;
import com.qihoo.zxc.constant.Constant;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.service.FloatWindowService;
import com.qihoo.zxc.utils.ShortCutUtil;
import com.qihoo.zxc.view.OptionCheckBox;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.Op;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends BaseActivity{
	
	OptionCheckBox openBoxCheckBox;
	OptionCheckBox autoHideCheckBox;
	OptionCheckBox speedUpCheckBox;
	OptionCheckBox screenShotCheckBox;
	ViewGroup hiddenSettingLayout;
	ViewGroup timeLineLayout;
	
	ViewGroup backLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting_activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void handleMsg(Message msg) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void setAllListeners() {
		
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(50);

		if (isServiceRunning(serviceList,
				"com.qihoo.zxc.service.FloatWindowService")) {
			// 已经运行了
			if (!LittleBoyApplication.getInstance().getFloatWindowsServiceStatus()) {
				// 除非有bug，不可能会遇到
				stopAllService();
				openBoxCheckBox.close();
			}
		} else {
			// 当前服务没执行
			if (LittleBoyApplication.getInstance().getFloatWindowsServiceStatus()) {
				startAllService();
			}else {
				openBoxCheckBox.close();
			}
		}
		
		openBoxCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startAllService();
				}else {
					stopAllService();
				}
				
			}
		});
		
		hiddenSettingLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,HiddenSettingActivity.class);
				startActivity(intent);
			}
		});
		
		timeLineLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShortCutUtil.createShortCut(SettingActivity.this);
			}
		});
		
		backLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});
	}

	@Override
	protected void findAllViews() {
		openBoxCheckBox = (OptionCheckBox) findViewById(R.id.open_box_checkbox);
		hiddenSettingLayout = (ViewGroup)findViewById(R.id.hidden_setting_layout);
		timeLineLayout = (ViewGroup)findViewById(R.id.time_line_layout);
		backLayout = (ViewGroup)findViewById(R.id.title_back_layout);
	}

	private boolean isServiceRunning(
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
	
	private void startAllService() {
		Intent intent = new Intent(SettingActivity.this,
				FloatWindowService.class);
		SettingActivity.this.startService(intent);
	}
	
	private void stopAllService() {
		Intent intent = new Intent(SettingActivity.this,
				FloatWindowService.class);
		SettingActivity.this.stopService(intent);
	}
}
