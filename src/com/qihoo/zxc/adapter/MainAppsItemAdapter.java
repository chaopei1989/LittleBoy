package com.qihoo.zxc.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.qihoo.zxc.R;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.model.PackageHidden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAppsItemAdapter extends BaseAdapter implements OnClickListener {

	private static MainAppsItemAdapter instance;

	public static MainAppsItemAdapter getInstance(Context context) {
		if (null == instance)
			instance = new MainAppsItemAdapter(context);
		return instance;
	}

	Set<Object> expandMap = new HashSet<>();

	class ViewHolder {
		TextView appname;
		TextView hide;
		TextView more;
		TextView open;
		ImageView hideImage;
		ImageView icon;
		ViewGroup gift;
		ViewGroup expand;
		ViewGroup expanded;
		ViewGroup strategy;
	}

	public void notifyDataSetChanged() {
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				MainAppsItemAdapter.super.notifyDataSetChanged();
			}
		});

	}

	List<PackageInfo> datas;
	Context context;
	PackageManager packageManager;

	private MainAppsItemAdapter(Context context) {
		this.context = context;
		packageManager = context.getPackageManager();
		datas = LittleBoyApplication.getInstance().packageInfoList;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder tag;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.main_page_item, null);
			tag = new ViewHolder();
			tag.appname = (TextView) convertView.findViewById(R.id.app_name);
			tag.open = (TextView) convertView.findViewById(R.id.open_textview);
			tag.icon = (ImageView) convertView.findViewById(R.id.app_icon);
			tag.hideImage = (ImageView) convertView
					.findViewById(R.id.hide_imageview);
			tag.strategy = (ViewGroup) convertView
					.findViewById(R.id.strategy_layout);
			tag.expand = (ViewGroup) convertView
					.findViewById(R.id.expand_layout);
			tag.expanded = (ViewGroup) convertView
					.findViewById(R.id.expanded_layout);
			tag.gift = (ViewGroup) convertView.findViewById(R.id.gift_layout);
			tag.hide = (TextView) convertView.findViewById(R.id.hide_textview);
			tag.more = (TextView) convertView.findViewById(R.id.more_textview);
			convertView.setTag(tag);
		} else {
			tag = (ViewHolder) convertView.getTag();
		}
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(
				datas.get(position).packageName);
		if (intent == null) {
			LittleBoyApplication.getInstance().gamesHiddenMap.remove(datas
					.get(position).packageName);
			LittleBoyApplication.getInstance().packageInfoList.remove(position);
			LittleBoyApplication.getInstance().notifyDataSetChanged();
		}
		ApplicationInfo applicationInfo = datas.get(position).applicationInfo;
		String appName = applicationInfo.loadLabel(packageManager).toString();
		tag.appname.setText(appName);
		tag.icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
		if (LittleBoyApplication.getInstance().gamesHiddenMap.containsKey(datas
				.get(position).packageName)) {
			PackageHidden packageHidden = LittleBoyApplication.getInstance().gamesHiddenMap
					.get(datas.get(position).packageName);
			if (packageHidden.hasGift > 0)
				tag.gift.setVisibility(View.VISIBLE);
			else
				tag.gift.setVisibility(View.GONE);
			if (!packageHidden.strategy.equals(""))
				tag.strategy.setVisibility(View.VISIBLE);
			else
				tag.strategy.setVisibility(View.GONE);
			if (packageHidden.hidden > 0) {
				tag.hideImage.setVisibility(View.VISIBLE);
				tag.hide.setText("ÏÔÊ¾Ðü¸¡¿ò");
			} else {
				tag.hideImage.setVisibility(View.GONE);
				tag.hide.setText("Òþ²ØÐü¸¡¿ò");
			}
		}

		tag.open.setTag(R.id.OPEN, datas.get(position).packageName);
		tag.open.setOnClickListener(this);
		tag.hide.setTag(R.id.HIDE, datas.get(position).packageName);
		tag.hide.setOnClickListener(this);
		tag.more.setTag(R.id.MORE, datas.get(position).packageName);
		tag.more.setOnClickListener(this);
		tag.expand.setTag(datas.get(position).packageName);
		tag.expand.setOnClickListener(this);

		if (expandMap.contains(datas.get(position).packageName)) {
			tag.expanded.setVisibility(View.VISIBLE);
		} else {
			tag.expanded.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		try {
			if (v instanceof ViewGroup) {
				if (expandMap.contains(v.getTag())) {
					expandMap.remove(v.getTag());
				} else {
					expandMap.add(v.getTag());
				}
				return;
			}
			if (null != v.getTag(R.id.OPEN)) {
				String packageName = (String) v.getTag(R.id.OPEN);
				try {
					Intent intent = context.getPackageManager()
							.getLaunchIntentForPackage(
									(String) v.getTag(R.id.OPEN));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					if (context instanceof Activity)
						((Activity) context).finish();
				} catch (Exception e) {
					LittleBoyApplication.getInstance().gamesHiddenMap
							.remove(packageName);
					for (PackageInfo info : LittleBoyApplication.getInstance().packageInfoList) {
						if (info.packageName.equals(packageName)) {
							LittleBoyApplication.getInstance().packageInfoList
									.remove(info);
							LittleBoyApplication.getInstance()
									.notifyDataSetChanged();
							break;
						}
					}
				}
				return;
			}
			if (null != v.getTag(R.id.HIDE)) {
				String pck = (String) v.getTag(R.id.HIDE);
				LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck,
						(LittleBoyApplication.getInstance().gamesHiddenMap
								.get(pck).hidden + 1) % 2);
				return;
			}
			if (null != v.getTag(R.id.MORE)) {
				String pck = (String) v.getTag(R.id.MORE);
				Intent intent = context.getPackageManager()
						.getLaunchIntentForPackage("com.qihoo.appstore");
				intent.putExtra("from_out_side", "feiyang");
				intent.putExtra("start_activity_index", 30);
				intent.putExtra("pkg_name", pck);
				context.startActivity(intent);
				return;
			}
		} finally {
			MainAppsItemAdapter.super.notifyDataSetChanged();
		}

	}
}
