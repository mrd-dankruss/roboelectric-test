package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.Paperless;
import com.mrdexpress.paperless.fragments.DriverHomeFragment;
import com.mrdexpress.paperless.fragments.ScanFragment;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;
import com.mrdexpress.paperless.net.ServerInterface;
import com.squareup.otto.Subscribe;

public class DriverHomeActivity extends Activity implements ScanFragment.ScanActivityInterface, DriverHomeFragment.DriverHomeFragmentInterface, ViewDeliveriesFragment.ViewDeliveriesFragmentInterface
{
    public final static int START_DELIVERY= 1;
    public final static int START_SCAN = 2;
    public final static int MANUAL_BARCODE= 3;
	
	/*private Fragment fragment;
    private Fragment scanFragment;
    private Fragment viewDeliveriesFragment;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Paperless.getInstance().setMainActivity(this);
        setContentView(R.layout.activity_home);

        showMenu();
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

    private void showMenu(){
        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);

        //if( existingFragment != null && ((Object)existingFragment).getClass() == DriverHomeFragment.class)
        //    fragment = existingFragment;
        //else
        Fragment fragment = new DriverHomeFragment();
        //fragment.setArguments( savedInstanceState);
        fm.beginTransaction().replace(R.id.activity_home_container, fragment).commit();
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

        //if (existingFragment != null && ((Object) existingFragment).getClass() == ViewDeliveriesFragment.class)
        //    viewDeliveriesFragment = existingFragment;
        //else
        Fragment  viewDeliveriesFragment = new ViewDeliveriesFragment();
        fm.beginTransaction().replace(R.id.activity_home_container, viewDeliveriesFragment).commit();
            //fm.beginTransaction().replace(R.id.activity_home_container, viewDeliveriesFragment).commit();
    }

    @Override
    public void startScan() {
        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);

        //if( existingFragment != null && ((Object)existingFragment).getClass() == ScanFragment.class)
        //    scanFragment = existingFragment;
        //else
        Fragment scanFragment = new ScanFragment();
        fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).commit();
        //fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).commit();
    }

    @Override
    public void viewDeliveriesDone() {
        showMenu();
        //FragmentManager fm = getFragmentManager();
        //Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
    }
}
