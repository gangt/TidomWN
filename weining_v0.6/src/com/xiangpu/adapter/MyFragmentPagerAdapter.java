package com.xiangpu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/6/24.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;
	private FragmentManager fm;

	public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.list = list;
		this.fm = fm;
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override  
    public Fragment instantiateItem(ViewGroup container, int position) {  
        Fragment fragment = (Fragment) super.instantiateItem(container,  
                position);  
        fm.beginTransaction().show(fragment).commit();  
        return fragment;  
    }  
  
    @Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
        // super.destroyItem(container, position, object);  
        Fragment fragment = list.get(position);  
        fm.beginTransaction().hide(fragment).commit();  
    }  
}
