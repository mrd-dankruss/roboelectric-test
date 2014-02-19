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

public class OverDeliveryDialog extends Dialog
{
	private Activity context;
	private TextView text_title, text_content_top, text_content_bottom;
	private ImageButton button_close;
	private Button button_ok;

	public OverDeliveryDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_over_delivery);
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		Typeface typeface_roboto_regular = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_REGULAR));
		
		text_title = (TextView) findViewById(R.id.text_over_delivery_name);
		text_content_top = (TextView) findViewById(R.id.text_over_delivery_content_top);
		text_content_bottom = (TextView) findViewById(R.id.text_over_delivery_content_bottom);
		button_close = (ImageButton) findViewById(R.id.button_over_delivery_closeButton);
		button_ok = (Button) findViewById(R.id.button_over_delivery_ok);
		
		text_title.setTypeface(typeface_roboto_bold);
		text_content_top.setTypeface(typeface_roboto_regular);
		text_content_bottom.setTypeface(typeface_roboto_regular);
		
		button_close.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		button_ok.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
	}
}
