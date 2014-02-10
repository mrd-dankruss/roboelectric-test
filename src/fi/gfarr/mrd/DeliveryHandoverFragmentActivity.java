package fi.gfarr.mrd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fi.gfarr.mrd.fragments.DeliveryHandoverFragment;
import fi.gfarr.mrd.fragments.ReportDelayListFragment;

public class DeliveryHandoverFragmentActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handover);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_handover_container);
		if (fragment == null)
		{
			fragment = new DeliveryHandoverFragment();
			fm.beginTransaction().add(R.id.activity_handover_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
