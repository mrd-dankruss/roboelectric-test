package fi.gfarr.mrd;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fi.gfarr.mrd.datatype.ComLogObject;
import fi.gfarr.mrd.db.Bag;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.fragments.MoreDialogFragment;
import fi.gfarr.mrd.fragments.MoreDialogFragment.SetNextDeliveryListener;
import fi.gfarr.mrd.fragments.UpdateStatusDialog;
import fi.gfarr.mrd.helper.FontHelper;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.widget.CustomToast;

public class DeliveryDetailsActivity extends FragmentActivity implements SetNextDeliveryListener
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
				intent.getStringExtra(VariableManager.EXTRA_BAG_NO));
	}

	@Override
	public void onResume()
	{
		super.onResume();

		holder.text_delivery_number.setText("#"
				+ Integer.parseInt(intent.getStringExtra(VariableManager.EXTRA_LIST_POSITION)) + 1);
		holder.text_delivery_title.setText("CURRENT MILKRUN DELIVERY"); // TODO: Change
		holder.text_delivery_addressee.setText("Addressee: " + bag.getDestinationHubName());
		holder.text_delivery_address.setText(bag.getDestinationAddress());
		holder.text_delivery_bad_id.setText("Bag number: " + bag.getBagNumber());
		// TODO: Remove hardcoded values

		ArrayList<ComLogObject> comlogs = DbHandler.getInstance(getApplicationContext()).getComLog(
				bag.getBagNumber());

		String comlog_text = "";

		for (int i = 0; i < comlogs.size(); i++)
		{
			comlog_text = "SMS sent at " + comlog_text + comlogs.get(i).getTimestamp() + "\n"
					+ comlogs.get(i).getNote() + "\n";
		}
		holder.text_delivery_communication_log.setText(comlog_text);

		// TODO:Set image here one day when app is extended.
		// holder.image_company_logo.setText("");

		// holder.button_update_status.setText("");
		// holder.button_more.setText("");

		holder.button_update_status.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				DialogFragment newFragment = UpdateStatusDialog.newInstance(bag.getBagNumber());
				newFragment.show(getSupportFragmentManager(), "dialog");
			}
		});

		holder.button_more.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				DialogFragment newFragment = MoreDialogFragment.newInstance(true,
						bag.getBagNumber());
				newFragment.show(getSupportFragmentManager(), "dialog");
			}
		});

	}

	@Override
	public void onSetNextDelivery(boolean is_successful)
	{
		if (is_successful)
		{
			CustomToast custom_toast = new CustomToast(this);
			custom_toast.setSuccess(true);
			custom_toast.setText("Successfully changed next delivery.");
			custom_toast.show();
			finish();
		}

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

			Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));
			Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_REGULAR));

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
			holder.text_delivery_communication_title = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_communicationTitle);
			holder.text_delivery_communication_log = (TextView) rootView
					.findViewById(R.id.deliveryDetails_textView_communicationLog);
			holder.image_company_logo = (ImageView) rootView
					.findViewById(R.id.deliveryDetails_imageView_companyLogo);
			holder.button_update_status = (Button) rootView
					.findViewById(R.id.deliveryDetails_button_updateStatus);
			holder.button_more = (Button) rootView.findViewById(R.id.deliveryDetails_button_more);

			holder.text_delivery_number.setTypeface(typeface_roboto_bold);
			holder.text_delivery_title.setTypeface(typeface_roboto_bold);
			holder.text_delivery_communication_title.setTypeface(typeface_roboto_bold);
			holder.button_update_status.setTypeface(typeface_roboto_bold);
			holder.button_more.setTypeface(typeface_roboto_bold);

			holder.text_delivery_addressee.setTypeface(typeface_roboto_regular);
			holder.text_delivery_address.setTypeface(typeface_roboto_regular);
			holder.text_delivery_bad_id.setTypeface(typeface_roboto_regular);
			holder.text_delivery_communication_log.setTypeface(typeface_roboto_regular);

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
		TextView text_delivery_communication_title;
		ImageView image_company_logo;
		Button button_update_status;
		Button button_more;
	}
}