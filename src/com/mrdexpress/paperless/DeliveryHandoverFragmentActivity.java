package com.mrdexpress.paperless;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.app.FragmentManager;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.fragments.DeliveryHandoverFragment;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONObject;

public class DeliveryHandoverFragmentActivity extends Activity
{
	private final String TAG = "DeliveryHandoverFragmentActivity";
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handover);

		// Fragment: Home Begin
		FragmentManager fm = getFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_handover_container);
		if (fragment == null)
		{
			fragment = new DeliveryHandoverFragment();
			fm.beginTransaction().add(R.id.activity_handover_container, fragment).commit();
		}
		// Fragment: Home End
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		// Check which request we're responding to
		if (requestCode == VariableManager.ACTIVITY_REQUEST_CODE_PARTIAL_DELIVERY)
		{
			// Make sure the request was successful
			if (resultCode == RESULT_OK)
			{
                Workflow.getInstance().setDeliveryStatus( Workflow.getInstance().currentBagID, Bag.STATUS_PARTIAL, "");
                Device.getInstance().displaySuccess("Delivery Logged." , this);
                finish();
                Intent intent = new Intent(getApplicationContext() , ViewDeliveriesFragmentActivity.class);
                startActivity(intent);
            }
		}
	}
}
