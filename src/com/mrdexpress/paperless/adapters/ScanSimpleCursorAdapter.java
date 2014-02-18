package com.mrdexpress.paperless.adapters;

import com.mrdexpress.paperless.db.DbHandler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import fi.gfarr.mrd.R;

public class ScanSimpleCursorAdapter extends SimpleCursorAdapter
{

	private Context context;
	private Cursor cursor;

	public ScanSimpleCursorAdapter(Context ctx, int layout, Cursor c, String[] from, int[] to,
			int flag)
	{
		super(ctx, layout, c, from, to, 0);
		context = ctx;
		cursor = c;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setViewText(TextView view, String text)
	{
		view.setText(text, BufferType.SPANNABLE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView text_view_consignment;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.row_scan, null, false);
			text_view_consignment = (TextView) convertView.findViewById(R.id.textView_row_scan);
			convertView.setTag(new ViewHolder(text_view_consignment));
		}
		else
		{
			ViewHolder holder = (ViewHolder) convertView.getTag();

			text_view_consignment = holder.text_view_consignment;
		}

		if (cursor != null)
		{
			cursor.moveToPosition(position);

			if (text_view_consignment != null)
			{

				DbHandler.getInstance(context);
				text_view_consignment.setText(cursor.getString(cursor
						.getColumnIndex(DbHandler.C_BAG_BARCODE))
						+ " ( "
						+ cursor.getString(cursor.getColumnIndex(DbHandler.C_BAG_NUM_ITEMS))
						+ " ITEMS )");
			}
		}

		return convertView;
	}

	public void setColour(int position, int color_res_id)
	{
		// if (cursor!=null & )
	}

	/**
	 * Creates static instances of resources. Increases performance by only
	 * finding and inflating resources only once.
	 **/
	private static class ViewHolder
	{

		public final TextView text_view_consignment;

		public ViewHolder(TextView text)
		{
			this.text_view_consignment = text;

		}
	}

}
