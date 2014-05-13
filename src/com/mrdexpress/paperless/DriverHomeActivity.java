package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.mrdexpress.paperless.channels.EventBus;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.fragments.DriverHomeFragment;
import com.mrdexpress.paperless.fragments.StopsFragment;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;
import com.mrdexpress.paperless.net.ServerInterface;
import com.squareup.otto.Subscribe;

public class DriverHomeActivity extends Activity implements StopsFragment.StopActivityInterface, DriverHomeFragment.DriverHomeFragmentInterface, ViewDeliveriesFragment.ViewDeliveriesFragmentInterface
{
    public final static int START_DELIVERY= 1;
    public final static int START_SCAN = 2;
    public final static int MANUAL_BARCODE= 3;
    public static Boolean EXTRA_STARTSCAN = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Paperless.getInstance().setMainActivity(this);
        setContentView(R.layout.activity_home);

        Bundle b = getIntent().getExtras();
        if (b != null){
            if (b.containsKey("start_scan")){
                if (b.getBoolean("start_scan")){
                    startScan();
                }
            }
        } else {
            showMenu();
        }
        //Paperless.getInstance().ottobus.unregister(this);
        Paperless.getInstance().ottobus.register( DriverHomeActivity.this );
	}

    @Subscribe
    public void eventbus(EventBus.ManagerBackToDriverHome em){
        String stopid = General.getInstance().getActivebagid();
        String driverid = General.getInstance().getReassigndriverid();
        ServerInterface.getInstance().reassignStop(stopid , driverid);
        Intent intent = new Intent(this, DriverHomeActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("start_scan", true);
        intent.putExtras(b);
        this.finish();
        startActivity(intent);
    }

    @Subscribe
    public void refreshwflow( EventBus.refreshWorkflow rw){
        startScan();
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
        //this.setTitle("Deliveries");
        //fragment.setArguments( savedInstanceState);
        fm.beginTransaction().replace(R.id.activity_home_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void startScan() {
        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);

        //if( existingFragment != null && ((Object)existingFragment).getClass() == ScanFragment.class)
        //    scanFragment = existingFragment;
        //else
        //Fragment scanFragment = new ScanFragment();
        //fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).commit();
        Fragment stopFragment = new StopsFragment();
        setTitle("Delivery Run Preperation");
        fm.beginTransaction().replace(R.id.activity_home_container, stopFragment).commit();
        //fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).commit();
    }

    @Override
    public void viewDeliveriesDone() {
        showMenu();
        //FragmentManager fm = getFragmentManager();
        //Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
    }

    @Override
    public void stopFragmentDone(int requestCode, int resultCode, Object data) {
        ServerInterface.getInstance().startTrip();

        FragmentManager fm = getFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
        this.setTitle("Deliveries");

        //if (existingFragment != null && ((Object) existingFragment).getClass() == ViewDeliveriesFragment.class)
        //    viewDeliveriesFragment = existingFragment;
        //else
        Fragment  viewDeliveriesFragment = new ViewDeliveriesFragment();
        fm.beginTransaction().replace(R.id.activity_home_container, viewDeliveriesFragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Paperless.getInstance().ottobus.unregister( DriverHomeActivity.this );
        }catch (Exception e){
            Log.e("MRD-EX" , e.getMessage());
        }
    }
}
