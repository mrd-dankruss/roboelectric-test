package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.mrdexpress.paperless.fragments.ReasonPartialDeliveryFragment;

public class ReasonPartialDeliveryActivity extends FragmentActivity
{

	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reason_partial_delivery);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_reason_partial_delivery_container);
		if (fragment == null)
		{
			fragment = new ReasonPartialDeliveryFragment();
			fm.beginTransaction().add(R.id.activity_reason_partial_delivery_container, fragment)
					.commit();
		}
		// Fragment: Home End
	}
}
