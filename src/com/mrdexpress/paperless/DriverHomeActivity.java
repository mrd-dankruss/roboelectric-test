package com.mrdexpress.paperless;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.DriverHomeFragment;
import com.mrdexpress.paperless.fragments.ScanFragment;
import com.mrdexpress.paperless.interfaces.FragmentResultInterface;

public class DriverHomeActivity extends Activity implements FragmentResultInterface
{
	
	Fragment fragment;
    Fragment scanFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        Paperless.getInstance().setMainActivity(this);

		// Fragment: Home Begin
		FragmentManager fm = getFragmentManager();
		Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
		if (fragment == null)
		{
            if( existingFragment != null && ((Object)existingFragment).getClass() == DriverHomeFragment.class)
                fragment = existingFragment;
            else
			    fragment = new DriverHomeFragment();
		}
        fragment.setArguments( savedInstanceState);
        fm.beginTransaction().replace(R.id.activity_home_container, fragment).commit();
		// Fragment: Home End
	}

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Intent data) {
        switch( requestCode)
        {
            case 2:
                FragmentManager fm = getFragmentManager();
                //scanFragment = fm.findFragmentById(R.id.activity_home_container);
                //if( scanFragment.getClass().equals( ScanFragment.class))
                Fragment existingFragment = fm.findFragmentById(R.id.activity_home_container);
                if (scanFragment == null)
                {
                    if( existingFragment != null && ((Object)existingFragment).getClass() == ScanFragment.class)
                        scanFragment = existingFragment;
                    else
                        scanFragment = new ScanFragment();
                }
                fm.beginTransaction().replace(R.id.activity_home_container, scanFragment).addToBackStack(null).commit();
                //Intent intent = new Intent( getApplicationContext(), ViewDeliveriesFragmentActivity.class);
                //startActivity(intent);
            break;
        }
        return false;
    }
}
