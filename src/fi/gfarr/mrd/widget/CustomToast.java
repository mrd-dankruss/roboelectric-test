package fi.gfarr.mrd.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import fi.gfarr.mrd.R;

public class CustomToast
{
	TextView text;
	Toast toast;
	View layout;

	public CustomToast(Activity activity, View v)
	{

		LayoutInflater inflater = activity.getLayoutInflater();
		layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) v.findViewById(R.id.toast_layout_root));

		text = (TextView) layout.findViewById(R.id.textView_toast);
		
		toast = new Toast(activity);
		toast.setGravity(Gravity.FILL_HORIZONTAL, 0, -280);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);

	}

	public void setSuccess(boolean goodNewsToast) {
		if (goodNewsToast)
		{
			layout.setBackgroundResource(R.drawable.toast_green);
		} else {
			layout.setBackgroundResource(R.drawable.toast_red);
		}
	}
	
	public void setText(String msg)
	{
		text.setText(msg);
	}

	public void show()
	{
		toast.show();
	}

}
