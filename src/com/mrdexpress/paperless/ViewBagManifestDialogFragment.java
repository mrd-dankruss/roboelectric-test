package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.fragments.ScanFragment;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;
import java.util.List;

public class ViewBagManifestDialogFragment extends DialogFragment
{
	private ViewHolder holder;
	private View root_view;
    ArrayList<DeliveryHandoverDataObject> list;
    private DeliveryHandoverAdapter listAdapter;


    public ViewBagManifestDialogFragment(CallBackFunction _callback) {
        callback = _callback;
    }

    private static CallBackFunction callback;

    public static ViewBagManifestDialogFragment newInstance(final CallBackFunction callback)
    {
        ViewBagManifestDialogFragment f = new ViewBagManifestDialogFragment( callback);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initViewHolder(inflater, container);

        return root_view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setContentView(R.layout.activity_view_bag_manifest);

        // Change actionbar title
        //setTitle(R.string.title_actionbar_manifest);

        holder.button_enterBarcode.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //intent.putExtra(MANAGER_AUTH_INCOMPLETE_SCAN, true);
                //setResult(ScanFragment.RESULT_MANUAL_ENTRY, intent);
                //finish();
                callback.execute(true);
                dismiss();
            }
        });

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        list = Workflow.getInstance().getBagParcelsAsObjects( savedInstanceState.getInt("bag_id"));
        listAdapter = new DeliveryHandoverAdapter(list);

        if ((listAdapter != null) & (list != null))
        {
            holder.list.setAdapter(listAdapter);
        }

        listAdapter.notifyDataSetChanged();

        // Remove padding from textview
        holder.text_view_consignment_destination.setIncludeFontPadding(false);
        holder.text_view_consignment_number.setIncludeFontPadding(false);

        // Set titles
        holder.text_view_consignment_number.setText(getString(R.string.text_consignment) + " " + Integer.toString( savedInstanceState.getInt("bag_id")) + " (" +  list.size() + " items)");
        holder.text_view_consignment_destination.setText(getString(R.string.text_destination_branch) + " " + savedInstanceState.getString("bag_dest"));
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
            final int thisPosition = position;

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_manifest, parent, false);
            //TextView parcelTitle = (TextView) rowView.findViewById(R.id.row_delivery_parcel);
            TextView waybillTile = (TextView) rowView.findViewById(R.id.textView_manifest_waybill);
            TextView volumetrics = (TextView) rowView.findViewById(R.id.textView_manifest_volumetrics);
            TextView barcode = (TextView) rowView.findViewById(R.id.textView_manifest_barcode);

            DeliveryHandoverDataObject dhdo = parcelList.get(thisPosition);

            waybillTile.setText( dhdo.getMDX() + " (" + dhdo.getXof() + ")");
            volumetrics.setText( dhdo.getVolumetrics());
            barcode.setText( dhdo.getBarcode());

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

        public int getScannedCount()
        {
            int scanned = 0;
            for( DeliveryHandoverDataObject item : this.parcelList )
            {
                if( item.isParcelScanned())
                    scanned++;
            }
            return scanned;
        }
    }

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.view_bag_manifest, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	} */

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
        if (root_view == null)
        {
            root_view = inflater.inflate(R.layout.activity_view_bag_manifest, container, false);

			//root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) root_view.findViewById(R.id.listView_manifest_consignment);
			holder.text_view_consignment_number = (TextView) root_view.findViewById(R.id.textView_manifest_consignment_number);
			holder.text_view_consignment_destination = (TextView) root_view.findViewById(R.id.textView_manifest_consignment_destination);
            holder.button_enterBarcode = (Button) root_view.findViewById(R.id.buttonManifestScanBarcode);
			// Store the holder with the view.
			root_view.setTag(holder);
		}
	}

	/**
	 * Creates static instances of resources. Increases performance by only
	 * finding and inflating resources only once.
	 **/
	static class ViewHolder
	{
		TextView text_view_consignment_number;
		TextView text_view_consignment_destination;
		// TextView text_view_manifest_weight;
        Button button_enterBarcode;
		ListView list;
	}

}
