package com.mrdexpress.paperless;

import com.mrdexpress.paperless.fragments.CallListFragment;
import com.mrdexpress.paperless.fragments.ReportDelayListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import fi.gfarr.mrd.R;

public class CallActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_call_container);
		if (fragment == null)
		{
			fragment = new CallListFragment();
			fm.beginTransaction().add(R.id.activity_call_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
