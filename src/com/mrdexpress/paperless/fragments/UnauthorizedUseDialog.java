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
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.helper.FontHelper;

public class UnauthorizedUseDialog extends Dialog
{
	private Activity context;
	private TextView dialog_title, dialog_imei;
	private ImageButton dialog_close;
    private Button okbutton;

	public UnauthorizedUseDialog(Activity activity)
	{
		super(activity);
		this.context = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_unauthorized_use);
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(context.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		
		dialog_title = (TextView) findViewById(R.id.textView_manAuth_manager_name);
		dialog_close = (ImageButton) findViewById(R.id.button_incomplete_scan_closeButton);
        dialog_imei = (TextView) findViewById(R.id.textView_unauth_imei);
		
		dialog_title.setTypeface(typeface_roboto_bold);
		
		dialog_close.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                System.exit(0);
			}
		});

        dialog_imei.setText( "IMEI: " + Device.getInstance().getIMEI());

        okbutton = (Button) findViewById(R.id.button_incomplete_scan_ok);
        okbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.exit(0);
            }
        });
	}
}
