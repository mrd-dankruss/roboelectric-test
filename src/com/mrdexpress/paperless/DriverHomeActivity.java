package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.fragments.DriverHomeFragment;
import com.mrdexpress.paperless.fragments.ScanFragment;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;
import com.mrdexpress.paperless.net.ServerInterface;
import com.squareup.otto.Subscribe;

public class DriverHomeActivity extends Activity implements ScanFragment.ScanActivityInterface, DriverHomeFragment.DriverHomeFragmentInterface
{
    public final static int START_DELIVERY= 1;
    public final static int START_SCAN = 2;
    public final static int MANUAL_BARCODE= 3;
	
	private Fragment fragment;
    private Fragment scanFragment;
    private Fragment viewDeliveriesFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Paperless.getInstance().setMainActivity(this);

        setContentView(R.layout.activity_home);

        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
        if (fragment == null)
        {
            if( existingFragment != null && ((Object)existingFragment).getClass() == DriverHomeFragment.class)
                fragment = existingFragment;
            else
                fragment = new DriverHomeFragment();
        }

        fm.beginTransaction().replace(R.id.activity_home_container, fragment).commit();
        Paperless.getInstance().ottobus.register(this);
	}

    @Subscribe
    public void mytestevent(String event){
        Log.e("MRD-EX", event);
        Device.getInstance().displayInfo(event , this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void scanFragmentDone(int requestCode, int resultCode, Object data) {
        ServerInterface.getInstance().startTrip();

        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
        if (viewDeliveriesFragment == null)
        {
            if( existingFragment != null && ((Object)existingFragment).getClass() == ViewDeliveriesFragment.class)
                viewDeliveriesFragment = existingFragment;
            else
                viewDeliveriesFragment = new ViewDeliveriesFragment();
            fm.beginTransaction().replace(R.id.activity_home_container, viewDeliveriesFragment).commit();
        }
    }

    @Override
    public void startScan() {
        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);

        if( existingFragment != null && ((Object)existingFragment).getClass() == ScanFragment.class)
            scanFragment = existingFragment;
        else
            scanFragment = new ScanFragment();

        fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).commit();
    }
}
