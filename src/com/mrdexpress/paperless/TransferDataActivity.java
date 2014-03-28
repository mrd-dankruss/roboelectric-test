package com.mrdexpress.paperless;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.mrdexpress.paperless.fragments.TransferDataFragment;

public class TransferDataActivity extends FragmentActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer_data);

		// Fragment: TransferData Begin
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.activity_transfer_data_container);
		if (fragment == null)
		{
			fragment = new TransferDataFragment();
			fm.beginTransaction().add(R.id.activity_transfer_data_container, fragment).commit();
		}
		// Fragment: TransferData End

	}

}
