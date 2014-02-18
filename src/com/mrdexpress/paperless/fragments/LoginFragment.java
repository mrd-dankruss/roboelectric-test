package com.mrdexpress.paperless.fragments;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.datatype.UserItem.UserType;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import fi.gfarr.mrd.R;

public class LoginFragment extends Fragment
{

	private final String TAG = "LoginFragment";
	private ViewHolder holder;
	private View rootView;
	private SharedPreferences prefs;
	ArrayList<UserItem> person_item_list;
	private String selected_user_id;
	private String selected_user_name;
	private UserType selected_user_type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		person_item_list = new ArrayList<UserItem>();

		prefs = getActivity().getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

		person_item_list.addAll(DbHandler.getInstance(getActivity().getApplicationContext())
				.getManagers());

		UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(getActivity()
				.getApplicationContext(), person_item_list);

		// Set the adapter
		holder.text_manager_name.setAdapter(adapter);
		holder.text_manager_name.setThreshold(1);

		holder.text_manager_name.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				selected_user_id = ((UserItem) holder.text_manager_name.getAdapter().getItem(
						position)).getUserID();
				selected_user_name = ((UserItem) holder.text_manager_name.getAdapter().getItem(
						position)).getUserName();
				selected_user_type = ((UserItem) holder.text_manager_name.getAdapter().getItem(
						position)).getUserType();

				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(holder.text_manager_name.getWindowToken(), 0);

			}
		});

		holder.button_login.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				new ManagerLoginUserTask().execute();
			}
		});
	}

	/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		((DialogDataObject) adapter.getItem(parentItemPosition)).setThirdText(data
				.getStringExtra(SMSDialog.DIALOG_TIME_STRING));

		// VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		// holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setBackgroundResource(R.drawable.button_custom);
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		holder.report_button.setEnabled(true);
	}
	*/

	/**
	 * Requests token from server.
	 * 
	 * @author htdahms
	 * 
	 */
	private class ManagerLoginUserTask extends AsyncTask<Void, Void, Boolean>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Authenticating");
			this.dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... urls)
		{
			String hash = PinManager.toMD5(holder.text_manager_pin.getText().toString());

			TelephonyManager mngr = (TelephonyManager) getActivity().getSystemService(
					Context.TELEPHONY_SERVICE);

			final String driver_id = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);

			String status = ServerInterface.getInstance(getActivity().getApplicationContext())
					.authManager(selected_user_id, driver_id, hash, mngr.getDeviceId());

			if (status.equals("success"))
			{
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{

			if (result == true)
			{
				// Close progress spinner
				if (dialog.isShowing())
				{
					dialog.dismiss();
				}

				prefs.edit().putString(VariableManager.LAST_LOGGED_IN_MANAGER_ID, selected_user_id)
						.commit();
				prefs.edit()
						.putString(VariableManager.LAST_LOGGED_IN_MANAGER_NAME, selected_user_name)
						.commit();

				getActivity().finish();
			}
		}
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_login_screen, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			Typeface typeface_roboto_bold = Typeface.createFromAsset(getActivity().getAssets(),
					FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));

			Typeface typeface_roboto_regular = Typeface.createFromAsset(getActivity().getAssets(),
					FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_REGULAR));

			holder.text_manager_name = (AutoCompleteTextView) rootView
					.findViewById(R.id.text_login_screen_name);
			holder.text_manager_pin = (TextView) rootView
					.findViewById(R.id.text_login_screen_password);
			holder.button_login = (Button) rootView.findViewById(R.id.button_login_screen);

			holder.text_manager_name.setTypeface(typeface_roboto_regular);
			holder.text_manager_pin.setTypeface(typeface_roboto_regular);
			holder.button_login.setTypeface(typeface_roboto_bold);

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
		AutoCompleteTextView text_manager_name;
		TextView text_manager_pin;
		Button button_login;
	}

}
