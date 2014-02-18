package com.mrdexpress.paperless.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fi.gfarr.mrd.R;

public class TransferDataFragment extends Fragment
{

	private ViewHolder holder;
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();

		final int totalProgressTime = 100;

		new TransferData().execute();
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_transfer_data, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.progress = (ProgressBar) rootView.findViewById(R.id.transfer_data_progress_bar);
			holder.image = (ImageView) rootView.findViewById(R.id.transfer_data_image);
			holder.text_message = (TextView) rootView.findViewById(R.id.transfer_data_message);
			holder.button_transfer = (Button) rootView.findViewById(R.id.transfer_data_button);

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
		ProgressBar progress;
		ImageView image;
		TextView text_message;
		Button button_transfer;
	}

	
	
	//JUST FOR TESTING
	public class TransferData extends AsyncTask<String, Integer, String>
	{

		String test;
		int count = 100;
		int totalSize = 0;

		@Override
		protected String doInBackground(String... params)
		{

			for (int i = 0; i <= count; i++)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				publishProgress(i);
				// Escape early if cancel() is called
				if (isCancelled()) break;
			}

			return null;
		}

		protected void onProgressUpdate(Integer... progress)
		{
			holder.progress.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result)
		{
			Log.d("fi.gfarr.mrd", "Done");
			holder.image.setImageResource(R.drawable.icon_uploadconplete_green_tick);
			holder.image.refreshDrawableState();
			holder.text_message.setText("Data transfer complete");
			holder.button_transfer.setText("Home");
			// adapter = new ArticleAdapter(articles);
			// setListAdapter(adapter);
		}
	}

}
