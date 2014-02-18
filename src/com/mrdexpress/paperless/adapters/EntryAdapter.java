package com.mrdexpress.paperless.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mrdexpress.paperless.R;

public class EntryAdapter extends ArrayAdapter<Item> {

	private Context context;
	private ArrayList<Item> items;
	private LayoutInflater vi;

	public EntryAdapter(Context context, ArrayList<Item> items) {
		super(context, 0, items);
		this.context = context;
		this.items = items;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		final Item i = items.get(position);
		if (i != null) {
			if (i.isSection()) {
				SectionItem si = (SectionItem) i;
				v = vi.inflate(R.layout.row_scan, null);

				v.setOnClickListener(null);
				v.setOnLongClickListener(null);
				v.setLongClickable(false);

				final TextView sectionView = (TextView) v
						.findViewById(R.id.textView_row_scan);
				sectionView.setText(si.getConsignmentNumber() + " ( "
						+ si.getConsignmentNumberItemsScanned() + " / "
						+ si.getConsignmentNumberItemsScanned());

			} else {
				EntryItem ei = (EntryItem) i;
				v = vi.inflate(R.layout.row_scan, null);
				final TextView title = (TextView) v
						.findViewById(R.id.textView_row_scan);

				if (title != null)
					title.setText(ei.cons_number);
				
			}
		}
		return v;
	}
}
