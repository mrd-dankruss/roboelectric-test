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

public class AlreadyScannedDialog extends Dialog
{
	private Activity context;
	private TextView dialog_title, dialog_msg;
	private ImageButton dialog_close;
	private Button dialog_ok;

	public AlreadyScannedDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_already_scanned);
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		
		dialog_title = (TextView) findViewById(R.id.text_already_scanned_name);
		dialog_msg = (TextView) findViewById(R.id.text_already_scanned_content);
		dialog_close = (ImageButton) findViewById(R.id.button_already_scanned_closeButton);
		dialog_ok = (Button) findViewById(R.id.button_already_scanned_ok);
		
		dialog_title.setTypeface(typeface_roboto_bold);
		
		dialog_close.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		dialog_ok.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
	}
	
	public void setTitle(String title)
	{
		dialog_title.setText(title);
	}
	
	public void setMessage(String msg)
	{
		dialog_msg.setText(msg);
	}
}
