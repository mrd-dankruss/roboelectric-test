package com.mrdexpress.paperless.adapters;

/**
 * Created by hannobean on 2014/03/26.
 */

import android.content.Context;
import android.database.Cursor;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import android.util.Log;
import com.mrdexpress.paperless.db.Drivers;

import java.util.ArrayList;

public class SelectDriverListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<Drivers.DriversObject> drivers;

    public SelectDriverListAdapter(Context context , ArrayList<Drivers.DriversObject> values)
    {
        super();
        mContext=context;
        drivers = values;
    }

    public int getCount()
    {
        // return the number of records in cursor
        return drivers.size();
    }

    // getView method is called for each item of ListView
    @Override
    public View getView(int position,  View view, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.row_driver_entry, null);
        TextView drivername =(TextView)view.findViewById(R.id.row_driver_name);
        drivername.setText(drivers.get(position).getfirstName() + " " + drivers.get(position).getlastName());
        return view;
    }

    @Override
    public Object getItem(int position)
    {
        return drivers.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
