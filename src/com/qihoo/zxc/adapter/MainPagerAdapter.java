package com.qihoo.zxc.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MainPagerAdapter extends PagerAdapter {

	private List<View> views = new ArrayList<View>();

	public MainPagerAdapter(View...views) {
		for (View view : views) {
			this.views.add(view);
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));// ɾ��ҳ��
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // �����������ʵ����ҳ��
		container.addView(views.get(position), 0);// ���ҳ��
		return views.get(position);
	}

	@Override
	public int getCount() {
		return views.size();// ����ҳ��������
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// �ٷ���ʾ����д
	}

}
