package fi.gfarr.mrd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import fi.gfarr.mrd.fragments.CallListFragment;
import fi.gfarr.mrd.fragments.ReportDelayListFragment;

public class CallActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_view_deliveries_content);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_viewDeliveries_container);
		if (fragment == null)
		{
			fragment = new CallListFragment();
			fm.beginTransaction().add(R.id.fragment_viewDeliveries_container, fragment).commit();
		}
		// Fragment: Home End
	}
}
