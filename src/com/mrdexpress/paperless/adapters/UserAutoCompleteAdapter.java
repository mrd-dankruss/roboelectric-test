package com.mrdexpress.paperless.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.db.Users;

public class UserAutoCompleteAdapter extends BaseAdapter implements Filterable
{

	private final String TAG = "UserAutoCompleteAdapter";

	private Context context;
	private ArrayList<UserItem> person_list;
	private ArrayList<UserItem> person_list_original;
    private ArrayList<Users.UserData> users_list;
    private ArrayList<Users.UserData> users_list_original;

	/*public UserAutoCompleteAdapter(Context context, ArrayList<UserItem> person_list)
	{
		this.context = context;
		this.person_list = person_list;
		this.person_list_original = person_list;
	}*/

    public UserAutoCompleteAdapter(Context context, ArrayList<Users.UserData> person_list)
    {
        this.context = context;
        this.users_list = person_list;
        this.users_list_original = person_list;
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
                users_list = users_list_original;

				FilterResults filterResults = new FilterResults();
				ArrayList<UserItem> result_list = new ArrayList<UserItem>();
                ArrayList<Users.UserData> result_list2 = new ArrayList<Users.UserData>();
				if (constraint != null)
				{
					// Retrieve the autocomplete results.
					for (int i = 0; i < users_list.size(); i++)
					{
						/*
						if ((person_list.get(i).getUserName().toLowerCase()).startsWith(constraint.toString().toLowerCase()))
						{
							Log.d(TAG, "person_list: " + person_list.get(i));
							result_list.add(person_list.get(i));
						}
						*/
                        if ((users_list.get(i).getFullName().toLowerCase()).contains(constraint.toString().toLowerCase()))
                        {
                            result_list2.add(users_list.get(i));
                        }
					}

					// Assign the data to the FilterResults
					filterResults.values = result_list2;
					filterResults.count = result_list2.size();
				}
				return filterResults;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				if (results != null && results.count > 0)
				{
					//person_list = (ArrayList<UserItem>)results.values;
                    users_list = (ArrayList<Users.UserData>)results.values;
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

		//text_person.setText(person_list.get(position).getUserName());
        text_person.setText(users_list.get(position).getFullName());

		return rowView;
	}

	@Override
	public int getCount()
	{
		/*
		if (person_list != null)
			return person_list.size();
		else return 0;
		*/
        if (users_list != null)
            return users_list.size();
        else return 0;
	}

	@Override
	public Object getItem(int position)
	{
        return users_list.get(position);
		//return person_list.get(position);
	}
}