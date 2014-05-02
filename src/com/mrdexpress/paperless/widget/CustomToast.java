package com.mrdexpress.paperless.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.mrdexpress.paperless.db.Paperless;
import com.mrdexpress.paperless.R;

public class CustomToast
{
	TextView text;
	Toast toast;
	View layout;
    public static String STYLE_INFO = "info";
    public static String STYLE_SUCCESS = "success";
    public static String STYLE_FAILED = "failed";

	public CustomToast(Activity activity)
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);

		layout = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) view.findViewById(R.id.toast_layout_root));

		text = (TextView) layout.findViewById(R.id.textView_toast);

		toast = new Toast(activity);

		toast.setGravity(Gravity.FILL_HORIZONTAL, 0, -250);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
	}

    public CustomToast(Context activity)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);

        View view = Paperless.getInstance().getActivity().getWindow().getDecorView().findViewById(android.R.id.content);

        layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) view.findViewById(R.id.toast_layout_root));

        text = (TextView) layout.findViewById(R.id.textView_toast);

        toast = new Toast(activity);

        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, -250);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
    }

    public CustomToast()
    {
        Context activity = Paperless.getContext();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = Paperless.getInstance().getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) view.findViewById(R.id.toast_layout_root));
        text = (TextView) layout.findViewById(R.id.textView_toast);
        toast = new Toast(activity);
        toast.setGravity(Gravity.FILL_HORIZONTAL, 0, -250);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
    }

	public CustomToast setSuccess(boolean goodNewsToast)
	{
		if (goodNewsToast)
		{
            layout.setBackgroundResource(R.color.colour_green_scan);
		}
		else
		{
            layout.setBackgroundResource(R.color.color_red);
		}
        return this;
	}

    public CustomToast setStyle(String style){
        if (style.equals(STYLE_INFO))
        {
            layout.setBackgroundResource(R.color.colour_yellow);
            text.setTextColor(Color.BLACK);
        }
        else if (style.equals(STYLE_SUCCESS))
        {
            layout.setBackgroundResource(R.color.colour_green_scan);
        }
        else if (style.equals(STYLE_FAILED))
        {
            layout.setBackgroundResource(R.color.color_red);
        }
        else
        {
            layout.setBackgroundResource(R.color.colour_yellow);
        }
        return this;
    }

	public CustomToast setText(String msg)
	{
		text.setText(msg);
        return this;
	}

	public void show()
	{
		toast.show();
	}

}
