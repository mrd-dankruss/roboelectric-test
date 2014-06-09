package com.mrdexpress.paperless;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.SmsListFragment;

public class SmsActivity extends Activity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);

		// Fragment: Home Begin
		FragmentManager fm = getFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_sms_container);
		if (fragment == null)
		{
			fragment = new SmsListFragment();
			fm.beginTransaction().add(R.id.activity_sms_container, fragment).commit();
		}
		// Fragment: Home End
	}
	
}
