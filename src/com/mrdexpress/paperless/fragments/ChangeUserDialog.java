package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.helper.FontHelper;

public class ChangeUserDialog extends Dialog
{
	private Activity context;
	private TextView dialog_title;

	public ChangeUserDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_change_user_confirm);

		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));

		dialog_title = (TextView) findViewById(R.id.textView_trafficDelay_title);
		
		dialog_title.setTypeface(typeface_roboto_bold);
	}

}