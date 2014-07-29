package com.qihoo.zxc.activity;

import com.qihoo.zxc.R;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class HiddenSettingActivity extends BaseActivity{

	ViewGroup backLayout;
	ListView listView;

	@Override
	protected void handleMsg(Message msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setAllListeners() {
		backLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HiddenSettingActivity.this.finish();
			}
		});
	}

	@Override
	protected void findAllViews() {
		backLayout = (ViewGroup)findViewById(R.id.title_back_layout);
		listView = (ListView)findViewById(R.id.hide_app_listview);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.hidden_setting_activity);
		super.onCreate(savedInstanceState);
	}

}
