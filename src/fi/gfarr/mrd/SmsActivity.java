package fi.gfarr.mrd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fi.gfarr.mrd.fragments.ReportDelayListFragment;
import fi.gfarr.mrd.fragments.SmsListFragment;

public class SmsActivity extends FragmentActivity
{
	
	Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_view_deliveries_content);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.fragment_viewDeliveries_container);
		if (fragment == null)
		{
			fragment = new SmsListFragment();
			fm.beginTransaction().add(R.id.fragment_viewDeliveries_container, fragment).commit();
		}
		// Fragment: Home End
		
		Button reportButton = (Button) findViewById(R.id.button_generic_report);
		reportButton.setVisibility(View.VISIBLE);
		reportButton.setText(R.string.button_send_sms);
		reportButton.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				sendMessageButton();
			}
		});
	}
	
	public void sendMessageButton() {
		Log.d("fi.gfarr.mrd", "Fragment Data: " + ((SmsListFragment)fragment).hasMessageSentSuccessfully());
	}
	
}
