package fi.gfarr.mrd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fi.gfarr.mrd.db.Bag;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;

public class DeliveryDetailsActivity extends FragmentActivity
{

	private ViewHolder holder;
	private View rootView;
	private Bag bag;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_delivery_details);

		initViewHolder();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		intent = getIntent();

		bag = DbHandler.getInstance(this).getBag(
				intent.getStringExtra(VariableManager.EXTRA_DRIVER_ID),
				intent.getStringExtra(VariableManager.EXTRA_BAG_NO));
	}

	@Override
	public void onResume()
	{
		super.onResume();

		Log.d("fi.gfarr.mrd", "Title: " + holder.text_delivery_title.getText());
		
		holder.text_delivery_number.setText(intent
				.getStringExtra(VariableManager.EXTRA_LIST_POSITION));
		holder.text_delivery_title.setText("CURRENT MILKRUN DELIVERY"); // TODO: Change
		holder.text_delivery_addressee.setText(bag.getDestinationHubName());
		holder.text_delivery_address.setText(bag.getDestinationAddress());
		holder.text_delivery_bad_id.setText(bag.getBagNumber());
		// TODO: Remove hardcoded values
		holder.text_delivery_communication_log.setText("SMS sent at 15:13\nRunning 5 minutes late");

		// TODO:Set image here one day when app is extended.
		//holder.image_company_logo.setText("");

		//holder.button_update_status.setText("");
		//holder.button_more.setText("");
		
		holder.button_update_status.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
			}
		});

		holder.button_more.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
			}
		});
		
	}

	public void initViewHolder()
	{

		if (rootView == null)
		{

			rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.text_delivery_number = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_deliveryNumber);
			holder.text_delivery_title = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_titleDetail);
			holder.text_delivery_addressee = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_addressee);
			holder.text_delivery_address = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_address);
			holder.text_delivery_bad_id = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_id);
			holder.text_delivery_communication_log = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_communicationLog);
			holder.image_company_logo = (ImageView) rootView
					.findViewById(R.id.deliveryDetails_imageView_companyLogo);
			holder.button_update_status = (Button) rootView
					.findViewById(R.id.deliveryDetails_button_updateStatus);
			holder.button_more = (Button) rootView.findViewById(R.id.deliveryDetails_button_more);

			// Store the holder with the view.
			rootView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
			{
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		TextView text_delivery_number;
		TextView text_delivery_title;
		TextView text_delivery_addressee;
		TextView text_delivery_address;
		TextView text_delivery_bad_id;
		TextView text_delivery_communication_log;
		ImageView image_company_logo;
		Button button_update_status;
		Button button_more;
	}
}