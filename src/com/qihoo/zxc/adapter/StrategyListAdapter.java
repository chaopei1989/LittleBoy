package com.qihoo.zxc.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.qihoo.zxc.R;
import com.qihoo.zxc.model.Strategy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class StrategyListAdapter extends BaseAdapter {
	final String TAG = "StrategyListAdapter";
	class Item {
		TextView title;
	}
	
	List<Strategy> strategies = new ArrayList<Strategy>();

	public Strategy get(int location) {
		return strategies.get(location);
	}
	
	Context context;
	
	String more = "";
	
	public void updateData(String json) {
		
	}
	
	public void name() {
		
	}
	
	public StrategyListAdapter(Context context) {
		this.context = context;
	}

	public StrategyListAdapter(Context context, String json) throws JSONException {
		this.context = context;
		JSONObject gl = new JSONObject(json);
		
		for (Iterator<String> iterator = gl.keys(); iterator.hasNext();) {
			String k = iterator.next();
			JSONObject ob = gl.getJSONObject(k);
			String id = "", url="", title="", s_title ="",thumbnail ="",channel="",channel_url="",more="";
			long re_time = 0;
			try {
				id = ob.getString("id");
			} catch (JSONException e) {
			}
			try {
				title = ob.getString("title");
			} catch (JSONException e) {
			}
			try {
				more = ob.getString("more");
				this.more = more;
			} catch (JSONException e) {
			}
			try {
				url = ob.getString("url");
			} catch (JSONException e) {
			}
			Log.e(TAG, title);
			strategies.add(new Strategy(id, title, url, s_title, thumbnail, channel, channel_url, re_time, more));
		}
		if(!this.more.equals(""))
			strategies.add(new Strategy("", "查看更多...", more, "", "", "", "", 0, ""));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return strategies.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return strategies.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView title;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.strategy_item, null);
			Item tag = new Item();
			tag.title = (TextView) convertView
					.findViewById(R.id.title_textview);
			convertView.setTag(tag);
		}
		title = ((Item) convertView.getTag()).title;
		title.setText(strategies.get(position).title);
		return convertView;
	}

}
