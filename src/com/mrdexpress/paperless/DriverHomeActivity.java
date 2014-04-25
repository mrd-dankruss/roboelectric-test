package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.mrdexpress.paperless.fragments.DriverHomeFragment;

public class DriverHomeActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
        Paperless.getInstance().setMainActivity(this);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_home_container);
		if (fragment == null)
		{
			fragment = new DriverHomeFragment();
            fragment.setArguments( savedInstanceState);
			fm.beginTransaction().add(R.id.activity_home_container, fragment).commit();
		}
		// Fragment: Home End
	}
	
}
