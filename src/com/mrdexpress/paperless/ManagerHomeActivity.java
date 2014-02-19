package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.mrdexpress.paperless.fragments.ManagerHomeFragment;

public class ManagerHomeActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_manager_container);
		if (fragment == null)
		{
			fragment = new ManagerHomeFragment();
			fm.beginTransaction().add(R.id.activity_manager_container, fragment).commit();
		}
		// Fragment: Home End
	}
	
}
