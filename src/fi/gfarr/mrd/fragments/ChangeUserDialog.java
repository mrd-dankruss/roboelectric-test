package fi.gfarr.mrd.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import fi.gfarr.mrd.R;

public class ChangeUserDialog extends Dialog //implements android.view.View.OnClickListener
{

	private final String TAG = "ChangeUserDialog";
	private Activity context;
	private Dialog dialog;
	private Button button_cancel, button_ok;	
	private ImageButton button_close;

	public ChangeUserDialog(Activity a)
	{
		super(a);
		// TODO Auto-generated constructor stub
		this.context = a;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_change_user_confirm);

		button_close = (ImageButton) findViewById(R.id.button_change_user_closeButton);
		button_cancel = (Button) findViewById(R.id.button_change_user_cancel);
		button_ok = (Button) findViewById(R.id.button_change_user_ok);

//		button_scan.setOnClickListener(this);
//		button_continue.setOnClickListener(this);
	}
	
/*
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.button_incomplete_scan_scan:
			// Log.d(TAG, "Scan them");
			// Do nothing. Close dialog.
			break;
		case R.id.button_incomplete_scan_continue:
			// Log.d(TAG, "Continue anyway");
			// Require manager authorization.		
			Intent intent = new Intent(context, ManagerAuthIncompleteScanActivity.class);
			
			break;
		default:
			break;
		}
		dismiss();
	}
	*/
}