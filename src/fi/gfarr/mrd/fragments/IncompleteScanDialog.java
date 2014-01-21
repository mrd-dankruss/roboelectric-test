package fi.gfarr.mrd.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import fi.gfarr.mrd.R;

public class IncompleteScanDialog extends Dialog implements android.view.View.OnClickListener
{

	private final String TAG = "IncompleteScanDialog";
	private Activity context;
	private Dialog dialog;
	private Button button_scan, button_continue;

	public IncompleteScanDialog(Activity a)
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
		setContentView(R.layout.dialog_incomplete_scan);

		button_scan = (Button) findViewById(R.id.button_incomplete_scan_scan);
		button_continue = (Button) findViewById(R.id.button_incomplete_scan_continue);

		button_scan.setOnClickListener(this);
		button_continue.setOnClickListener(this);
	}

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

			break;
		default:
			break;
		}
		dismiss();
	}
}
