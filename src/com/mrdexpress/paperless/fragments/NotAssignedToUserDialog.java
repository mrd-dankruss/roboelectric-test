package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.helper.FontHelper;

public class NotAssignedToUserDialog extends Dialog
{
	private Activity context;
	private TextView dialog_title, dialog_content;
	private ImageButton dialog_close;
	private Button dialog_cancel, dialog_continue;

	public NotAssignedToUserDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_not_assigned_to_user);
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		
		Typeface typeface_roboto_regular = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_REGULAR));
		
		dialog_title = (TextView) findViewById(R.id.textView_not_assigned_name);
		dialog_close = (ImageButton) findViewById(R.id.button_not_assigned_closeButton);
		dialog_cancel = (Button) findViewById(R.id.button_not_assigned_cancel);
		dialog_continue = (Button) findViewById(R.id.button_not_assigned_continue);
		dialog_content = (TextView) findViewById(R.id.text_not_assigned_content);
		
		dialog_title.setTypeface(typeface_roboto_bold);
		dialog_cancel.setTypeface(typeface_roboto_bold);
		dialog_continue.setTypeface(typeface_roboto_bold);
		dialog_content.setTypeface(typeface_roboto_regular);
		
		dialog_close.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		dialog_cancel.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
	}
}
