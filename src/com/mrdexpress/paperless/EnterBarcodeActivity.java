package com.mrdexpress.paperless;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.mrdexpress.paperless.helper.FontHelper;

public class EnterBarcodeActivity extends FragmentActivity
{

	private final String TAG = "EnterBarcodeFragment";
	private ViewHolder holder;
	private View rootView;
	public final static String MANUAL_BARCODE = "MANUAL_BARCODE";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_enter_barcode);

		initViewHolder();

		holder.button_ok.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.putExtra(MANUAL_BARCODE, holder.text_barcode.getText().toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		InputMethodManager imgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public void initViewHolder()
	{

		if (rootView == null)
		{

			rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));

			holder.text_barcode = (EditText) rootView.findViewById(R.id.text_enter_barcode);
			holder.button_ok = (Button) rootView.findViewById(R.id.button_enter_barcode_ok);

			holder.button_ok.setTypeface(typeface_roboto_bold);

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
		EditText text_barcode;
		Button button_ok;
	}
}
