package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.mrdexpress.paperless.fragments.ReasonForFailedHandoverFragment;

public class ReasonForFailedHandoverActivity extends FragmentActivity
{
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_delay);
        Paperless.getInstance().setMainActivity(this);
		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_report_delay_container);
		if (fragment == null)
		{
			fragment = new ReasonForFailedHandoverFragment();
			fm.beginTransaction().add(R.id.activity_report_delay_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
