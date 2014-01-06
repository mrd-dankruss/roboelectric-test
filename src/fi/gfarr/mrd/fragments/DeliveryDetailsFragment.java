package fi.gfarr.mrd.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.R.layout;

public class DeliveryDetailsFragment extends Fragment {
	
	private ViewHolder holder;
	private View rootView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		initViewHolder(inflater, container); // Inflate ViewHolder static instance
		
		
		return rootView;
    }
	
	
	public void initViewHolder(LayoutInflater inflater, ViewGroup container) {

		if (rootView == null) {

			rootView = inflater.inflate(R.layout.fragment_delivery_details, null, false);

			if (holder == null) {
				holder = new ViewHolder();
			}

			//holder.list = (ListView) rootView.findViewById(listId);


			// Store the holder with the view.
			rootView.setTag(holder);

		} else {
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null)
					&& (rootView.getParent() instanceof ViewGroup)) {
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			} else {
			}
		}
	}
	
	//Creates static instances of resources.
	//Increases performance by only finding and inflating resources only once.
	static class ViewHolder {
		
	}		
}
