package fi.gfarr.mrd.adapters;

import java.util.List;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.fragments.MoreDialogFragment;
import fi.gfarr.mrd.fragments.UpdateStatusDialog;

public class ViewDeliveriesListAdapter extends BaseAdapter
{
	private final FragmentActivity activity;
	private final Context context;
	List<List<String>> values;
	private ImageView deliveryType, companyLogo;
	private TextView deliveryNumber, titleDetail, address, id;
	private Button updateStatus, more;
	private LinearLayout buttonsHolder;

	public enum DeliveryType {
		DELIVERY, RETURN, EXCHANGE
	}

	public enum Company {
		FNB, TAKEALOT, NONE
	}

	/**
	 * @param context
	 *            The current context.
	 * @param values
	 *            The values to be used in the list in a two-dimensional
	 *            arrayList (to enable onSetDataChanged).
	 *            ArrayList(DeliveryType, Company, Address, Delivery ID).
	 *            DeliveryType and Company can be called through their
	 *            respective enums. <br>
	 * 
	 *            Delivery Type: DELIVERY, RETURN, EXCHANGE Company: FNB,
	 *            TAKEALOT <br>
	 * 
	 * <br>
	 *            Example: <br>
	 * 
	 *            List<List<String>> values = new ArrayList<List<String>>(); <br>
	 * 
	 *            List<String> temp1 = new ArrayList<String>(); <br>
	 *            temp1.add(DeliveryType.DELIVERY.toString()); <br>
	 *            temp1.add(Company.FNB.toString()); <br>
	 *            temp1.add(
	 *            "Mr D Brackenfell\n12 Goede Hoop Ave,\nBrackenfell\n7526"); <br>
	 *            temp1.add("00025420254 (6 items)"); <br>
	 *            values.add(temp1); <br>
	 */
	public ViewDeliveriesListAdapter(FragmentActivity activity, List<List<String>> values)
	{
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		/*
		Typeface typeface_robotoRegular = Typeface.createFromAsset(activity.getAssets(), FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF, FontHelper.STYLE_REGULAR));
		Typeface typeface_robotoLight = Typeface.createFromAsset(activity.getAssets(), FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF, FontHelper.STYLE_ITALIC));
		Typeface typeface_robotoBold = Typeface.createFromAsset(activity.getAssets(), FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF, FontHelper.STYLE_BOLD));
		*/

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_deliveries, parent, false);

		// Reference each view item and set required fonts
		deliveryType = (ImageView) rowView.findViewById(R.id.deliveries_imageView_deliveryType);

		deliveryNumber = (TextView) rowView.findViewById(R.id.deliveries_textView_deliveryNumber);
		//deliveryNumber.setTypeface(typeface_robotoBold);

		titleDetail = (TextView) rowView.findViewById(R.id.deliveries_textView_titleDetail);
		//titleDetail.setTypeface(typeface_robotoBold);

		companyLogo = (ImageView) rowView.findViewById(R.id.deliveries_imageView_companyLogo);

		address = (TextView) rowView.findViewById(R.id.deliveries_textView_address);
		//address.setTypeface(typeface_robotoLight);

		id = (TextView) rowView.findViewById(R.id.deliveries_textView_id);
		//id.setTypeface(typeface_robotoRegular);

		buttonsHolder = (LinearLayout) rowView.findViewById(R.id.deliveries_linearLayout_buttonsHolder);

		updateStatus = (Button) rowView.findViewById(R.id.deliveries_button_updateStatus);
		//updateStatus.setTypeface(typeface_robotoBold);

		more = (Button) rowView.findViewById(R.id.deliveries_button_more);
		//more.setTypeface(typeface_robotoBold);

		if (position == 0)
		{
			deliveryType.setImageResource(getDeliveryTypeIcon(DeliveryType.valueOf(values.get(position).get(0))));

			if (position < 10)
			{
				deliveryNumber.setText("#0" + (position + 1));
			} else
			{
				deliveryNumber.setText("#" + (position + 1));
			}

			titleDetail.setText(getTitle(DeliveryType.valueOf(values.get(position).get(0))));

			int companyLogoID = getCompanyIcon(Company.valueOf(values.get(position).get(1)));
			if (companyLogoID != 0)
			{
				companyLogo.setImageResource(companyLogoID);
			}

			address.setText(values.get(position).get(2));
			id.setText(values.get(position).get(3));

			updateStatus.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					DialogFragment newFragment = UpdateStatusDialog.newInstance(10);
					newFragment.show(activity.getSupportFragmentManager(), "dialog");
				}
			});

			more.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					DialogFragment newFragment = MoreDialogFragment.newInstance(false);
					newFragment.show(activity.getSupportFragmentManager(), "dialog");
				}
			});

		} else
		{

			deliveryType.setImageResource(getDeliveryTypeIcon(DeliveryType.valueOf(values.get(position).get(0))));

			if (position < 10)
			{
				deliveryNumber.setText("#0" + (position + 1));
			} else
			{
				deliveryNumber.setText("#" + (position + 1));
			}

			titleDetail.setText(values.get(position).get(0));

			int companyLogoID = getCompanyIcon(Company.valueOf(values.get(position).get(1)));
			if (companyLogoID != 0)
			{
				companyLogo.setImageResource(companyLogoID);
			}

			address.setText(values.get(position).get(2));
			id.setText(values.get(position).get(3));
			buttonsHolder.setVisibility(View.GONE);
		}

		return rowView;
	}

	private String getTitle(DeliveryType type)
	{
		switch (type)
		{
			case DELIVERY:
				return "YOUR NEXT DELIVERY";

			case RETURN:
				return "YOUR NEXT RETURN";

			case EXCHANGE:
				return "YOUR NEXT EXCHANGE";

			default:
				return "YOUR NEXT DELIVERY";
		}
	}

	private int getDeliveryTypeIcon(DeliveryType type)
	{
		switch (type)
		{
			case DELIVERY:
				return R.drawable.icon_delivery;

			case RETURN:
				return R.drawable.icon_return;

			case EXCHANGE:
				return R.drawable.icon_exchange;

			default:
				return R.drawable.icon_delivery;
		}
	}

	private int getCompanyIcon(Company company)
	{
		switch (company)
		{
			case FNB:
				return R.drawable.icon_fnb;

			case TAKEALOT:
				return R.drawable.icon_takealot;

			case NONE:
				return 0;

			default:
				return 0;
		}
	}

	@Override
	public int getCount()
	{
		return values.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}