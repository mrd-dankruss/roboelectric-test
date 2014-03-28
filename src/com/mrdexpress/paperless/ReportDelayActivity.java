package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.mrdexpress.paperless.fragments.ReportDelayListFragment;

public class ReportDelayActivity extends FragmentActivity
{
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_delay);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_report_delay_container);
		if (fragment == null)
		{
			fragment = new ReportDelayListFragment();
			fm.beginTransaction().add(R.id.activity_report_delay_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
