package com.qihoo.zxc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	String TAG;
	public Handler handler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				handleMsg(msg);
				super.handleMessage(msg);
			}
			
		};
		findAllViews();
		setAllListeners();
	}
	protected abstract void handleMsg(Message msg);
	protected abstract void setAllListeners();
	protected abstract void findAllViews();

}
