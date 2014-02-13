package fi.gfarr.mrd.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.ReasonPartialDeliveryActivity;
import fi.gfarr.mrd.datatype.DeliveryHandoverDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.widget.CustomToast;

public class DeliveryHandoverFragment extends Fragment
{
	private final String TAG = "DeliveryHandoverFragment";
	private ViewHolder holder;
	private View rootView;
	private IncompleteScanDialog dialog;
	ArrayList<DeliveryHandoverDataObject> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		initViewHolder(inflater, container); // Inflate ViewHolder static
												// instance

		/*	list = new ArrayList<DeliveryHandoverDataObject>();
			list.add(new DeliveryHandoverDataObject("GS24SD34D3", true));
			list.add(new DeliveryHandoverDataObject("4597531024", true));
			list.add(new DeliveryHandoverDataObject("4564564568", true));
			list.add(new DeliveryHandoverDataObject("4561234623", true));
			list.add(new DeliveryHandoverDataObject("1234568764", true));
			list.add(new DeliveryHandoverDataObject("4612387135", true));
			list.add(new DeliveryHandoverDataObject("8795431364", true));
			list.add(new DeliveryHandoverDataObject("4513234687", true));
			list.add(new DeliveryHandoverDataObject("3456023456", true));
			list.add(new DeliveryHandoverDataObject("7864313456", true));
			list.add(new DeliveryHandoverDataObject("1237613554", true));
			list.add(new DeliveryHandoverDataObject("7789995442", true));
			list.add(new DeliveryHandoverDataObject("2222346456", true));*/

		String bagid = getActivity().getIntent().getStringExtra(VariableManager.EXTRA_NEXT_BAG_ID);
		// Log.d(TAG, "Zorro - Bag ID: " + bagid);

		list = DbHandler.getInstance(getActivity()).getWaybillsForHandover(bagid);

		final DeliveryHandoverAdapter adapter = new DeliveryHandoverAdapter(list);
		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
			{

			}
		});

		holder.button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (allParcelsScanned())
				{
					getActivity().finish();
					CustomToast toast = new CustomToast(getActivity());
					toast.setSuccess(true);
					toast.setText("Delivery completed successfully!");
					toast.show();

				}
				else
				{

					dialog = new IncompleteScanDialog(getActivity());
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					dialog.show();

					LayoutInflater factory = LayoutInflater.from(getActivity());

					final Button button_continue = (Button) dialog
							.findViewById(R.id.button_incomplete_scan_continue);

					button_continue.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							/*
							FragmentTransaction ft = getActivity().getSupportFragmentManager()
									.beginTransaction();
							Fragment reasonFragment = new ReasonPartialDeliveryFragment();
							ft.replace(R.id.activity_reason_partial_delivery_container,
									reasonFragment);
							ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
							ft.addToBackStack(null);
							ft.commit();*/
							Intent intent = new Intent(getActivity(),
									ReasonPartialDeliveryActivity.class);

							Bundle b = new Bundle();
							/*b.putParcelableArrayList(
									VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE,
									getUnscannedParcels(list));*/
							intent.putExtra(VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE,
									getUnscannedParcels(list));
							intent.putExtra(VariableManager.EXTRA_NEXT_BAG_ID, getActivity()
									.getIntent().getStringExtra(VariableManager.EXTRA_NEXT_BAG_ID));
							// intent.putExtra(VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE, b);
							// intent.putExtra(VariableManager.EXTRA_BAG_NO,
							// ((Bag)holder.list.getItemAtPosition(position)).getBagNumber());
							// startActivity(intent);
							getActivity().startActivityForResult(intent,
									VariableManager.ACTIVITY_REQUEST_CODE_PARTIAL_DELIVERY);
						}
					});

					final Button button_scan = (Button) dialog
							.findViewById(R.id.button_incomplete_scan_scan);

					button_scan.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
						}
					});
				}
			}
		});

		return rootView;
	}

	/**
	 * Checks list of parcels and returns a list of parcels which have not yet been scanned.
	 * 
	 * @param list
	 * @return
	 */
	private String[] getUnscannedParcels(ArrayList<DeliveryHandoverDataObject> list)
	{
		ArrayList<String> arraylist_unscanned = new ArrayList<String>();

		// TODO conver arraylist to array properly
		for (int i = 0; i < list.size(); i++)
		{
			if (!list.get(i).isParcelScanned())
			{
				// list_unscanned[i] = (list.get(i).getParcelID());
				arraylist_unscanned.add(list.get(i).getParcelID());
			}
		}
		String[] list_unscanned = new String[arraylist_unscanned.size()];

		for (int i = 0; i < arraylist_unscanned.size(); i++)
		{
			list_unscanned[i] = arraylist_unscanned.get(i);
			Log.d(TAG, "unscanned bagid: " + i + ") " + list_unscanned[i]);
		}

		return list_unscanned;
	}

	/**
	 * Checks if all parcels have been scanned into the branch
	 * 
	 * @return True boolean value if all parcels have been scanned.
	 */
	private boolean allParcelsScanned()
	{

		boolean allScanned = true;

		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i).isParcelScanned() == false)
			{
				allScanned = false;
				break;
			}
		}

		return allScanned;
	}

	private class DeliveryHandoverAdapter extends BaseAdapter
	{

		List<DeliveryHandoverDataObject> parcelList;

		public DeliveryHandoverAdapter(ArrayList<DeliveryHandoverDataObject> objects)
		{
			super();
			parcelList = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.row_delivery_handover, parent, false);

			TextView parcelTitle = (TextView) rowView
					.findViewById(R.id.row_delivery_handover_title);
			ImageView hasScannedParcel = (ImageView) rowView
					.findViewById(R.id.row_delivery_handover_image);

			parcelTitle.setText(parcelList.get(position).getBarcode());
			if (parcelList.get(position).isParcelScanned() == true)
			{
				parcelTitle.setTextColor(getResources().getColor(R.color.green_tick));
				hasScannedParcel.setVisibility(View.VISIBLE);
			}

			return rowView;
		}

		@Override
		public int getCount()
		{
			return parcelList.size();
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

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_delivery_handover, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) rootView
					.findViewById(R.id.deliveryHandover_listView_scannedParcels);
			holder.button = (Button) rootView.findViewById(R.id.button_delivery_handover_complete);

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
		ListView list;
		Button button;
	}
}
