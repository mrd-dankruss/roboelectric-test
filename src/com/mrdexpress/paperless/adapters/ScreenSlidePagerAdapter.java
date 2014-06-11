package com.mrdexpress.paperless.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import com.mrdexpress.paperless.fragments.TabViewDeliveriesFragment;


/**
 * A simple pager adapter that represents TabViewDeliveriesFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

	/**
	 * The number of pages (wizard steps) to show in this demo.
	 */
	private int NUM_PAGES;

	
	public ScreenSlidePagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public ScreenSlidePagerAdapter(FragmentManager fragmentManager, int pages) {
		super(fragmentManager);
		NUM_PAGES = pages;
	}

	@Override
	public Fragment getItem(int position) {
		Log.d("fi.gfarr.mrd", "Position: " + position);
		Fragment fragment = null;
		switch (position) {
            case 0:	fragment = new TabViewDeliveriesFragment();
            case 1: fragment = new TabViewDeliveriesFragment();
            case 2: fragment = new TabViewDeliveriesFragment();
        }
		return fragment;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}