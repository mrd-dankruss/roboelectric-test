package com.mrdexpress.paperless;

import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.fragments.TransferDataFragment;

public class TransferDataActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_data);

		// Fragment: TransferData Begin
		FragmentManager fm = getFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_transfer_data_container);
		if (fragment == null)
		{
			fragment = new TransferDataFragment();
			fm.beginTransaction().add(R.id.activity_transfer_data_container, fragment).commit();
		}
		// Fragment: TransferData End

	}

}
