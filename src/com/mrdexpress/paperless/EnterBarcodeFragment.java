package com.mrdexpress.paperless;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentResultInterface;

public class EnterBarcodeFragment extends DialogFragment
{
	private final String TAG = "EnterBarcodeFragment";
	private ViewHolder holder;
	private View rootView;
	public final static String MANUAL_BARCODE = "MANUAL_BARCODE";

    public EnterBarcodeFragment( CallBackFunction _callback) {
        callback = _callback;
    }

    public interface barcodeListener{
        public void cancel();
        public void gotBarcode( String barcode);
    }

    private static CallBackFunction callback;

    public static EnterBarcodeFragment newInstance(final CallBackFunction callback)
    {
        EnterBarcodeFragment f = new EnterBarcodeFragment( callback);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initViewHolder(inflater, container);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        holder.text_barcode.setOnKeyListener( new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    holder.button_ok.callOnClick();
                    return true;
                }
                return false;
            }
        });


        holder.button_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //intent.putExtra(MANUAL_BARCODE, holder.text_barcode.getText().toString());
                //setResult(Activity.RESULT_OK, intent);
                //finish();
                callback.execute(holder.text_barcode.getText().toString());
                dismiss();
            }
        });

    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
		if (rootView == null)
		{
            rootView = inflater.inflate(R.layout.fragment_enter_barcode, container, false);
            //getActivity().setContentView();

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			Typeface typeface_roboto_bold = Typeface.createFromAsset( getActivity().getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));

			holder.text_barcode = (EditText) rootView.findViewById(R.id.text_enter_barcode);
			holder.button_ok = (Button) rootView.findViewById(R.id.button_enter_barcode_ok);

			holder.button_ok.setTypeface(typeface_roboto_bold);

			rootView.setTag(holder);
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		EditText text_barcode;
		Button button_ok;
	}
}
