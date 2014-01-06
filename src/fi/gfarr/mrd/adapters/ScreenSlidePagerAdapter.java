package fi.gfarr.mrd.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import fi.gfarr.mrd.fragments.ViewDeliveriesFragment;



/**
 * A simple pager adapter that represents ViewDeliveriesFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

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
		return new ViewDeliveriesFragment();
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}