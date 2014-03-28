package com.mrdexpress.paperless.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;



/**
 * A simple pager adapter that represents ViewDeliveriesFragment objects, in
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
            case 0:	fragment = new ViewDeliveriesFragment();
            case 1: fragment = new ViewDeliveriesFragment();
            case 2: fragment = new ViewDeliveriesFragment();
        }
		return fragment;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}