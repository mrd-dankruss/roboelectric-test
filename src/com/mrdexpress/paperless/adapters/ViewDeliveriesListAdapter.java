package com.mrdexpress.paperless.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.app.DialogFragment;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.fragments.MoreDialogFragment;
import com.mrdexpress.paperless.fragments.UpdateStatusDialog;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.ui.ViewHolder;

import java.util.List;

public class ViewDeliveriesListAdapter extends BaseAdapter
{
	private final String TAG = "ViewDeliveriesListAdapter";
	private final Activity activity;
	private final Context context;
	List<Bag> values;
	private ImageView deliveryType, companyLogo;
	private TextView deliveryNumber, titleDetail, address, id;
	private Button updateStatus, more;
	private LinearLayout buttonsHolder;
	private int bag_id;

	public enum DeliveryType
	{
		DELIVERY, RETURN, EXCHANGE, FOOD_DELIVERY
	}

	public enum Company
	{
		FNB, TAKEALOT, NONE, MRD
	}

	public ViewDeliveriesListAdapter(Activity activity, List<Bag> values)
	{
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.values = values;
	}

	@Override
	public View getView(int position, View rowView, ViewGroup parent)
	{
		Typeface typeface_roboto_bold = Typeface.createFromAsset(activity.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));

        if( rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_deliveries, parent, false);
        }

		// TODO: Uncomment for food deliveries
		// FIXME: Move all row related items inside if or else blocks
		/*
		if (delivery_type == DeliveryType.FOOD_DELIVERY)
		{
			rowView = inflater.inflate(R.layout.row_food_deliveries, parent, false);
		} else {
			rowView = inflater.inflate(R.layout.row_deliveries, parent, false);
		}
		*/

		// Reference each view item and set required fonts
		deliveryType = ViewHolder.get( rowView, R.id.deliveries_imageView_deliveryType);

		deliveryNumber = ViewHolder.get( rowView, R.id.deliveries_textView_deliveryNumber);
		deliveryNumber.setTypeface(typeface_roboto_bold);

		titleDetail = ViewHolder.get( rowView, R.id.deliveries_textView_titleDetail);
		titleDetail.setTypeface(typeface_roboto_bold);

		companyLogo = ViewHolder.get( rowView, R.id.deliveries_imageView_companyLogo);

		address = ViewHolder.get( rowView, R.id.deliveries_textView_address);
		// address.setTypeface(typeface_robotoLight);

		id = (TextView) ViewHolder.get( rowView, R.id.deliveries_textView_id);
		// id.setTypeface(typeface_robotoRegular);

		buttonsHolder = ViewHolder.get( rowView, R.id.deliveries_linearLayout_buttonsHolder);

		updateStatus = ViewHolder.get( rowView, R.id.deliveries_button_updateStatus);
		updateStatus.setTypeface(typeface_roboto_bold);

		more = ViewHolder.get( rowView, R.id.deliveries_button_more);
		more.setTypeface(typeface_roboto_bold);
        //more.setText("More Options");
		
		// Icon
		// Only doing Milkruns for now so hardcode
		deliveryType.setImageResource(this.getDeliveryTypeIcon(DeliveryType.DELIVERY));
		
		// Leading zero
		if (position < 10)
		{
			deliveryNumber.setText("#0" + (position + 1));
		}
		else
		{
			deliveryNumber.setText("#" + (position + 1));
		}
		
		// Address
		address.setText(MiscHelper.getBagFormattedAddress(values.get(position)));

		// ID
		id.setText(values.get(position).getBarcode() + " ("	+ values.get(position).getNumberItems() + " items)");
		
		// Company logo
		// Only doing MrD for now so hardcode
		// int companyLogoID = getCompanyIcon(Company.valueOf(values.get(position).get(1)));
		int companyLogoID = getCompanyIcon(Company.MRD);
		if (companyLogoID != 0)
		{
			companyLogo.setImageResource(companyLogoID);
		}
		
		Bag bag = values.get(position);
		boolean isNextBag = false;
		int nextBagId = MiscHelper.getNextDeliveryId(activity);
		if( nextBagId != -1)
		{
			isNextBag = nextBagId == bag.getBagID();
		}
		else
		{
			isNextBag = (position == 0);
		}
		
		// Delivery type
		titleDetail.setText(getTitle(DeliveryType.DELIVERY, isNextBag));
		
		if (isNextBag)
		{
			final int bag_id = bag.getBagID();

			updateStatus.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					DialogFragment newFragment = UpdateStatusDialog.newInstance(bag_id, new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            notifyDataSetChanged();
                            return true;
                        }
                    });
					newFragment.show(activity.getFragmentManager(), "dialog");
				}
			});

			more.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					DialogFragment newFragment = MoreDialogFragment.newInstance(false, bag_id);
					newFragment.show(activity.getFragmentManager(), "dialog");
				}
			});
		}
		else
		{
			buttonsHolder.setVisibility(View.GONE);
		}

		return rowView;
	}

	private String getTitle(DeliveryType type, boolean isNextDelivery)
	{
		if (!isNextDelivery)
			return "FOLLOWED BY";
		
		switch (type)
		{
		case DELIVERY:
			return "YOUR NEXT DELIVERY";

		case RETURN:
			return "YOUR NEXT RETURN";

		case EXCHANGE:
			return "YOUR NEXT EXCHANGE";

		case FOOD_DELIVERY:
			return "YOUR NEXT FOOD DELIVERY";

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

		case FOOD_DELIVERY:
			return R.drawable.icon_food;

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

		case MRD:
			return R.drawable.mrd_logo_small;

		default:
			return 0;
		}
	}
	

	@Override
	public int getCount()
	{
		try
		{
			return values.size();
		}
		catch (NullPointerException e)
		{
			Log.e(TAG, "get(): NullPointerException");
			return 0;
		}
	}

	@Override
	public Object getItem(int position)
	{
		return values.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
}