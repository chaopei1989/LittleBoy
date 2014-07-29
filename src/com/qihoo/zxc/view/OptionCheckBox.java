package com.qihoo.zxc.view;


import com.qihoo.zxc.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class OptionCheckBox extends CheckBox{

	int srcOn = R.drawable.common_switch_green_enable;
	int srcOff = R.drawable.common_switch_green_disable;
	
	@Override
	protected void onAttachedToWindow() {
		if (isSelected()) {
			setButtonDrawable(srcOn);
		}else {
			setButtonDrawable(srcOff);
		}

		super.onAttachedToWindow();
	}
	public OptionCheckBox(Context context) {
		super(context);
	}
	public void setSrc(int on,int off) {
		srcOff = off;
		srcOn = on;
	}
	public OptionCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public OptionCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChecked(boolean checked) {
		setSelected(checked);
		if (checked) {
			setButtonDrawable(srcOn);
		}else {
			setButtonDrawable(srcOff);
		}
		super.setChecked(checked);
	}
	
	public void close() {
		setChecked(false);
	}
	public void open() {
		setChecked(true);
	}
}
