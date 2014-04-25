package com.mrdexpress.paperless;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.ReasonForFailedHandoverFragment;

public class ReasonForFailedHandoverActivity extends Activity
{
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_delay);

		// Fragment: Home Begin
		FragmentManager fm = getFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_report_delay_container);
		if (fragment == null)
		{
			fragment = new ReasonForFailedHandoverFragment();
			fm.beginTransaction().add(R.id.activity_report_delay_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
