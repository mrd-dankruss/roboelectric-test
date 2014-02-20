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

public class GenericTwoButtonDialog extends Dialog
{
	private Activity context;
	private TextView text_title, text_message;
	private ImageButton button_close;
	private Button button_top, button_bottom;

	public GenericTwoButtonDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_generic_two_button);
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		
		text_title = (TextView) findViewById(R.id.generic_two_dialog_text_name);
		text_message = (TextView) findViewById(R.id.generic_two_dialog_text_message);
		button_close = (ImageButton) findViewById(R.id.generic_two_dialog_image_button_close);
		button_top = (Button) findViewById(R.id.generic_two_dialog_button_button_top);
		button_bottom = (Button) findViewById(R.id.generic_two_dialog_button_button_bottom);
		
		text_title.setTypeface(typeface_roboto_bold);
		
		button_close.setOnClickListener(new View.OnClickListener()
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
		text_title.setText(title);
	}
	
	public void setMessage(String msg)
	{
		text_message.setText(msg);
	}
	
	public void setButtonTopTitle(String title)
	{
		button_top.setText(title);
	}
	
	public void setButtonBottomTitle(String title)
	{
		button_bottom.setText(title);
	}
}
