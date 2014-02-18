package com.mrdexpress.paperless.adapters;

import com.mrdexpress.paperless.fragments.CompletedDeliveriesFragment;
import com.mrdexpress.paperless.fragments.UnsuccessfulDeliveriesFragment;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new CompletedDeliveriesFragment();
        case 1:
            // Games fragment activity
            return new ViewDeliveriesFragment();
        case 2:
            // Movies fragment activity
            return new UnsuccessfulDeliveriesFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}