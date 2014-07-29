package com.qihoo.zxc.activity;

import java.util.List;

import com.qihoo.zxc.R;
import com.qihoo.zxc.adapter.MainAppsItemAdapter;
import com.qihoo.zxc.adapter.MainPagerAdapter;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.service.FloatWindowService;
import com.qihoo.zxc.utils.ProcessUtil;
import com.qihoo.zxc.utils.ShortCutUtil;
import com.qihoo.zxc.view.OptionCheckBox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends BaseActivity {

	ViewPager mainPager;
	MainPagerAdapter adapter;
	View selectLine;
	// ViewGroup backLaypout;

	ListView appListView;
	MainAppsItemAdapter appListApdater;

	private TextView[] textViews = new TextView[2];
	private OptionCheckBox openBoxCheckBox;
	private ViewGroup createShortCutLayout;

	@Override
	protected void handleMsg(Message msg) {
	}
	@Override
	protected void onResume() {
		LittleBoyApplication.getInstance().notifyDataSetChanged();
		super.onResume();
	}
	@Override
	protected void setAllListeners() {
		Resources resources = getResources();
		final ColorStateList textBlack = resources
				.getColorStateList(R.color.text_black);
		final ColorStateList textGreen = resources
				.getColorStateList(R.color.text_green);

		mainPager.setAdapter(adapter);
		mainPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				textViews[arg0].setTextColor(textGreen);
				textViews[(arg0 + 1) % 2].setTextColor(textBlack);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.d(TAG, arg0 + ":" + arg1 + "," + arg2);
				if (arg0 == 0) {
					int l = (int) (selectLine.getWidth() * arg1);
					int r = (int) (selectLine.getWidth() * (arg1 + 1));
					selectLine.layout(l, selectLine.getTop(), r,
							selectLine.getBottom());
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		appListView.setAdapter(appListApdater);
		// backLaypout.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
		for (int i = 0; i < textViews.length; i++) {
			final int fi = i;
			textViews[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mainPager.setCurrentItem(fi);
				}
			});
		}
		LittleBoyApplication.getInstance().registerAdapter(appListApdater);
		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(50);

		if (ProcessUtil.isServiceRunning(serviceList,
				"com.qihoo.zxc.service.FloatWindowService")) {
			// 已经运行了
			if (!LittleBoyApplication.getInstance()
					.getFloatWindowsServiceStatus()) {
				// 除非有bug，不可能会遇到
				stopAllService();
				openBoxCheckBox.close();
			}
		} else {
			// 当前服务没执行
			if (LittleBoyApplication.getInstance()
					.getFloatWindowsServiceStatus()) {
				startAllService();
			} else {
				openBoxCheckBox.close();
			}
		}

		openBoxCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							startAllService();
						} else {
							stopAllService();
						}

					}
				});
		createShortCutLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShortCutUtil.createShortCut(MainActivity.this);
			}
		});
	}

	@Override
	protected void findAllViews() {
		mainPager = (ViewPager) findViewById(R.id.main_pager);
		LayoutInflater lf = LayoutInflater.from(this);
		View view2 = lf.inflate(R.layout.setting_page, null);
		View view1 = lf.inflate(R.layout.main_page, null);
		appListView = (ListView) view1.findViewById(R.id.apps_listview);
		adapter = new MainPagerAdapter(view1, view2);
		// backLaypout = (ViewGroup)findViewById(R.id.title_back_layout);
		selectLine = findViewById(R.id.selected_line);
		textViews[0] = (TextView) findViewById(R.id.first_textview);
		textViews[1] = (TextView) findViewById(R.id.second_textview);
		appListApdater = MainAppsItemAdapter.getInstance(this);
		openBoxCheckBox = (OptionCheckBox) view2
				.findViewById(R.id.open_box_checkbox);
		createShortCutLayout = (ViewGroup) view2.findViewById(R.id.create_shortcut_layout);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main_activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		LittleBoyApplication.getInstance().unRegisterAdapter(appListApdater);
		super.onDestroy();
	}

	private void startAllService() {
		Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
		MainActivity.this.startService(intent);
	}

	private void stopAllService() {
		Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
		MainActivity.this.stopService(intent);
	}
}
