package com.qihoo.zxc.activity;

import com.qihoo.zxc.R;
import com.qihoo.zxc.adapter.ShortCutAdapter;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.utils.ShortCutUtil;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

public class ShortCutActivity extends BaseActivity {

	ViewGroup layout;
	GridView gridView;
	ShortCutAdapter adapter;
	ViewGroup moreLayout;
	ImageView searchImageView;
	EditText searchEditText;
	private TextView titleTextView;

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
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String packageName = LittleBoyApplication.getInstance().packageInfoList
						.get(position).packageName;
				try {
					Intent intent = getPackageManager()
							.getLaunchIntentForPackage(packageName);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				} catch (Exception e) {
					e.printStackTrace();
					LittleBoyApplication.getInstance().gamesHiddenMap
							.remove(packageName);
					LittleBoyApplication.getInstance().packageInfoList
							.remove(position);
					LittleBoyApplication.getInstance().notifyDataSetChanged();
				}

			}
		});
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Vibrator v = (Vibrator)
				// getSystemService(Context.VIBRATOR_SERVICE);
				// v.vibrate(5);
				String packageName = LittleBoyApplication.getInstance().packageInfoList
						.get(position).packageName;
				PackageHidden packageHidden = LittleBoyApplication
						.getInstance().gamesHiddenMap.get(packageName);
				if (packageHidden == null) {// 不会出现。。。

				}
				if (packageHidden.hidden > 0) {// 现在是隐藏，要将其打开
					LittleBoyApplication.getInstance().myHelper.saveAppHidden(
							packageName, 0);
					view.findViewById(R.id.hide_imageview).setVisibility(
							View.GONE);
				} else {
					LittleBoyApplication.getInstance().myHelper.saveAppHidden(
							packageName, 1);
					view.findViewById(R.id.hide_imageview).setVisibility(
							View.VISIBLE);
				}
				return true;
			}
		});
		moreLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShortCutUtil.searchInAppstore("精品游戏", ShortCutActivity.this);
				finish();
			}
		});
		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				

				String str = searchEditText.getText().toString();
				if (!str.equals("")) {
					ShortCutUtil.searchInAppstore(str, ShortCutActivity.this);
					finish();
					return;
				}else {
					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}

				ValueAnimator animator = ValueAnimator.ofInt(0,
						2 * layout.getWidth() / 3);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					ViewGroup.LayoutParams params = searchEditText
							.getLayoutParams();

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int v = (int) animation.getAnimatedValue();
						params.width = v;
						searchEditText.setLayoutParams(params);
					}
				});
				animator.setInterpolator(AnimationUtils.loadInterpolator(
						ShortCutActivity.this,
						android.R.anim.decelerate_interpolator));
				animator.setDuration(200);
				animator.start();
				titleTextView.setVisibility(View.INVISIBLE);
			}
		});
		searchEditText.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() != KeyEvent.ACTION_UP) {
					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					String str = searchEditText.getText().toString();
					if (!str.equals("")) {
						ShortCutUtil.searchInAppstore(str, ShortCutActivity.this);
						finish();
					}
				}
				return false;

			}

		});
	}

	@Override
	protected void findAllViews() {
		layout = (ViewGroup) findViewById(R.id.short_cut_layout);
		gridView = (GridView) findViewById(R.id.short_cut_gridview);
		titleTextView = (TextView) findViewById(R.id.title_textview);
		adapter = ShortCutAdapter.getInstance(this);
		moreLayout = (ViewGroup) findViewById(R.id.more_layout);
		searchImageView = (ImageView) findViewById(R.id.search_imageview);
		searchEditText = (EditText) findViewById(R.id.search_edittext);
		LittleBoyApplication.getInstance().registerAdapter(adapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.short_cut_activity);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		LittleBoyApplication.getInstance().unRegisterAdapter(adapter);
		super.onDestroy();
	}

}
