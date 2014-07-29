package com.qihoo.zxc.adapter;

import java.util.List;

import com.qihoo.zxc.R;
import com.qihoo.zxc.constant.LittleBoyApplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShortCutAdapter extends BaseAdapter{
	private static ShortCutAdapter instance;
	public static ShortCutAdapter getInstance(Context context) {
		if(null == instance)
			instance = new ShortCutAdapter(context);
		return instance;
	}
	
	class ViewHolder{
		TextView appname;
		ImageView icon;
		ImageView hide;
	}
	public void notifyDataSetChanged() {
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ShortCutAdapter.super.notifyDataSetChanged();
			}
		});
		
    }
	List<PackageInfo> datas;
	Context context;
	PackageManager packageManager;
	private ShortCutAdapter(Context context){
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.short_cut_item, null);
			ViewHolder tag = new ViewHolder();
			tag.appname = (TextView) convertView.findViewById(R.id.item_textview);
			tag.icon = (ImageView)convertView.findViewById(R.id.item_imageview);
			tag.hide = (ImageView)convertView.findViewById(R.id.hide_imageview);
			convertView.setTag(tag);
		}
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(
				datas.get(position).packageName);
		if (intent == null) {
			LittleBoyApplication.getInstance().gamesHiddenMap.remove(datas
					.get(position).packageName);
			LittleBoyApplication.getInstance().packageInfoList.remove(position);
			LittleBoyApplication.getInstance().notifyDataSetChanged();
		}
		ViewHolder tag = (ViewHolder) convertView.getTag();
		ApplicationInfo applicationInfo = datas.get(position).applicationInfo;
		String appName = applicationInfo.loadLabel(packageManager).toString();
		tag.appname.setText(appName);
		tag.icon.setImageDrawable(applicationInfo.loadIcon(packageManager));
		if (LittleBoyApplication.getInstance().gamesHiddenMap.containsKey(datas.get(position).packageName)) {
			if(LittleBoyApplication.getInstance().gamesHiddenMap.get(datas.get(position).packageName).hidden>0)
				tag.hide.setVisibility(View.VISIBLE);
			else
				tag.hide.setVisibility(View.GONE);
		}
		return convertView;
	}

}
