package com.mrdexpress.paperless;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.CallListFragment;

public class CallActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);

		// Fragment: Home Begin
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_call_container);
		if (fragment == null)
		{
			fragment = new CallListFragment();
			fm.beginTransaction().add(R.id.activity_call_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
