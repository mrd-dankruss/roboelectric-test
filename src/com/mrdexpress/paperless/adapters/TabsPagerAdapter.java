package com.mrdexpress.paperless.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import com.mrdexpress.paperless.fragments.TabCompletedDeliveriesFragment;
import com.mrdexpress.paperless.fragments.TabPartialDeliveriesFragment;
import com.mrdexpress.paperless.fragments.TabViewDeliveriesFragment;
import com.mrdexpress.paperless.fragments.TabUnsuccessfulDeliveriesFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new TabViewDeliveriesFragment();
        case 1:
            return new TabCompletedDeliveriesFragment();
        case 2:
            return new TabPartialDeliveriesFragment();
        case 3:
            return new TabUnsuccessfulDeliveriesFragment();
        }
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}