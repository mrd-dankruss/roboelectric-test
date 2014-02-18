package com.mrdexpress.paperless;

import com.mrdexpress.paperless.fragments.ReportDelayListFragment;
import com.mrdexpress.paperless.fragments.SmsListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fi.gfarr.mrd.R;

public class SmsActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_sms_container);
		if (fragment == null)
		{
			fragment = new SmsListFragment();
			fm.beginTransaction().add(R.id.activity_sms_container, fragment).commit();
		}
		// Fragment: Home End
	}
	
}
