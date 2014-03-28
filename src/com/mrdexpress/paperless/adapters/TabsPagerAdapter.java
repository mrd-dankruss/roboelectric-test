package com.mrdexpress.paperless.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.mrdexpress.paperless.fragments.CompletedDeliveriesFragment;
import com.mrdexpress.paperless.fragments.PartialDeliveriesFragment;
import com.mrdexpress.paperless.fragments.UnsuccessfulDeliveriesFragment;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;
 
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            return new ViewDeliveriesFragment();
        case 1:
            return new CompletedDeliveriesFragment();
        case 2:
            return new PartialDeliveriesFragment();
        case 3:
            return new UnsuccessfulDeliveriesFragment();
        }
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}