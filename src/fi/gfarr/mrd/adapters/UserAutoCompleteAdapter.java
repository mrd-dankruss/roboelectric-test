package fi.gfarr.mrd.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.datatype.UserItem;

public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable
{

	private final String TAG = "UserAutoCompleteAdapter";

	private Context context;
	private ArrayList<UserItem> person_list;
	private ArrayList<UserItem> person_list_original;

	public UserAutoCompleteAdapter(Context context, ArrayList<UserItem> person_list)
	{
		this.context = context;
		this.person_list = person_list;
		this.person_list_original = person_list;
	}

	@Override
	public Filter getFilter()
	{
		Filter filter = new Filter()
		{
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				person_list = person_list_original;
				FilterResults filterResults = new FilterResults();
				ArrayList<UserItem> result_list = new ArrayList<UserItem>();
				if (constraint != null)
				{
					// Retrieve the autocomplete results.
					for (int i = 0; i < person_list.size(); i++)
					{
						if ((person_list.get(i).getUserName().toLowerCase()).contains(constraint))
						{
							Log.d(TAG, "person_list: " + person_list.get(i));
							result_list.add(person_list.get(i));
						}
					}

					// Assign the data to the FilterResults
					filterResults.values = result_list;
					filterResults.count = result_list.size();
				}
				Log.d(TAG, "person_list: " + filterResults.count);
				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				if (results != null && results.count > 0)
				{
					person_list = (ArrayList<UserItem>)results.values;
					notifyDataSetChanged();
				}
				else
				{
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_driverlist, parent, false);

		TextView text_person = (TextView) rowView.findViewById(R.id.textView_row_driverlist);

		text_person.setText(person_list.get(position).getUserName());

		return rowView;
	}

	@Override
	public int getCount()
	{
		if (person_list != null)
			return person_list.size();
		else return 0;
	}

	@Override
	public Object getItem(int position)
	{
		return person_list.get(position);
	}
}